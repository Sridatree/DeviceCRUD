package org.interview.devicecrud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.interview.devicecrud.aggregator.DeviceCrudAggregator;
import org.interview.devicecrud.exception.*;
import org.interview.devicecrud.model.DeviceCreationRequest;
import org.interview.devicecrud.model.DeviceUpdationRequest;
import org.interview.devicecrud.service.DeviceCrudService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;


import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DeviceCrudController.class)
@ContextConfiguration(classes = {
        DeviceCrudController.class,
        DeviceCrudControllerAdvice.class
})
@ExtendWith(MockitoExtension.class)
class DeviceCrudControllerAdviceTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean DeviceCrudAggregator aggregator;
    @MockBean DeviceCrudService    service;

    private String creationJson() throws Exception {
        DeviceCreationRequest req = DeviceCreationRequest.builder()
                .name("Galaxy S24")
                .brand("Samsung")
                .state("AVAILABLE")
                .creationTime(LocalDateTime.now())
                .build();
        return objectMapper.writeValueAsString(req);
    }

    private String updateJson() throws Exception {
        DeviceUpdationRequest req = DeviceUpdationRequest.builder()
                .name("Galaxy S25")
                .brand("Samsung")
                .state("AVAILABLE")
                .build();
        return objectMapper.writeValueAsString(req);
    }

    @Test
    void duplicateDevice_returns409() throws Exception {
        doThrow(new DuplicateDeviceException("Device with same name and brand already exists."))
                .when(aggregator).createDevice(any());

        mvc.perform(post("/private/v1/device/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message",
                        is("Device with same name and brand already exists.")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void invalidCreation_returns400() throws Exception {
        doThrow(new InvalidDeviceCreationException("Name must not be blank"))
                .when(aggregator).createDevice(any());

        mvc.perform(post("/private/v1/device/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Name must not be blank"));
    }


    @Test
    void invalidUpdate_returns400() throws Exception {
        doThrow(new InvalidDeviceUpdateException("Illegal state transition"))
                .when(aggregator).updateDevice(eq("123"), any());

        mvc.perform(put("/private/v1/device/update/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Illegal state transition"));
    }

    @Test
    void invalidId_returns400() throws Exception {
        doThrow(new InvalidDeviceIdException("Device ID must not be blank.", ""))
                .when(aggregator).getDeviceById("blank");

        mvc.perform(get("/private/v1/device/fetch/blank"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Device ID must not be blank."));
    }

    /* ==================================================================
     * 5. InvalidDeletionException   400 BAD_REQUEST  (DELETE /{id})
     * ================================================================== */
    @Test
    void invalidDeletion_returns400() throws Exception {
        doThrow(new InvalidDeletionException("Cannot delete device that is in use"))
                .when(aggregator).deleteDevice("IN-USE-ID");

        mvc.perform(delete("/private/v1/device/IN-USE-ID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot delete device that is in use"));
    }

    @Test
    void mongoError_returns500() throws Exception {
        doThrow(new MongoDBException("Failed to update brand", "duplicate key"))
                .when(service).updateBrandIfNotInUse(eq("ABC"), eq("Apple"));

        mvc.perform(put("/private/v1/device/updateBrand/ABC/Apple"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message",
                        is("Failed to update brand duplicate key")));
    }


    @Test
    void illegalArgument_returns400() throws Exception {
        doThrow(new IllegalArgumentException("Invalid or blank device state."))
                .when(aggregator).fetchDevices(eq("Samsung"), eq("BAD"));

        mvc.perform(get("/private/v1/device/fetch")
                        .param("brand", "Samsung")
                        .param("state", "BAD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid or blank device state."));
    }


    @Test
    void genericError_returns500() throws Exception {
        doThrow(new RuntimeException("Boom"))
                .when(aggregator).getDeviceById("999");

        mvc.perform(get("/private/v1/device/fetch/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."));
    }
}
