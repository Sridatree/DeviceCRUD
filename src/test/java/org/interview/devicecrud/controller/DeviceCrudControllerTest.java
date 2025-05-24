package org.interview.devicecrud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.interview.devicecrud.aggregator.DeviceCrudAggregator;
import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.exception.DuplicateDeviceException;
import org.interview.devicecrud.model.Device;
import org.interview.devicecrud.model.DeviceCreationRequest;
import org.interview.devicecrud.model.DeviceUpdationRequest;
import org.interview.devicecrud.service.DeviceCrudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class DeviceCrudControllerTest {

    @Mock  DeviceCrudAggregator aggregator;
    @Mock  DeviceCrudService    service;

    private MockMvc  mockMvc;


    @BeforeEach
    void setUp() {
        DeviceCrudController controller = new DeviceCrudController(aggregator, service);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new DeviceCrudControllerAdvice())
                .build();
    }

    @Test
    void create_returns201AndBody() throws Exception {
        String rawJson = """
        {
          "name": "Galaxy S24",
          "brand": "Samsung",
          "state": "AVAILABLE",
          "creationTime": "2024-05-24T10:15:30"
        }
        """;

        Device created = Device.builder()
                .id("SAMSUNG_GALAXY_S24")
                .name("Galaxy S24")
                .brand("Samsung")
                .state(DeviceState.AVAILABLE)
                .creationTime(LocalDateTime.of(2024, 5, 24, 10, 15, 30))
                .build();

        when(aggregator.createDevice(any())).thenReturn(created);

        mockMvc.perform(post("/private/v1/device/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rawJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("SAMSUNG_GALAXY_S24"))
                .andExpect(jsonPath("$.brand").value("Samsung"));

        ArgumentCaptor<DeviceCreationRequest> captor =
                ArgumentCaptor.forClass(DeviceCreationRequest.class);
        verify(aggregator).createDevice(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Galaxy S24");
    }


    @Test
    void create_whenDuplicateDeviceException_returns409() throws Exception {
        when(aggregator.createDevice(any()))
                .thenThrow(new DuplicateDeviceException("Device exists"));

        mockMvc.perform(post("/private/v1/device/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Device exists"));
    }

    @Test
    void update_returns200AndBody() throws Exception {
        String id = "D-001";
        ObjectMapper json = new ObjectMapper();
        DeviceUpdationRequest updReq = DeviceUpdationRequest.builder()
                .name("XPS 14").brand("Dell").state("AVAILABLE").build();

        Device updated = Device.builder()
                .id(id).name("XPS 14").brand("Dell")
                .state(DeviceState.AVAILABLE).creationTime(LocalDateTime.now()).build();

        when(aggregator.updateDevice(eq(id), any())).thenReturn(updated);

        mockMvc.perform(put("/private/v1/device/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(updReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("XPS 14"));

        verify(aggregator).updateDevice(eq(id), any(DeviceUpdationRequest.class));
    }


    @Test
    void updateBrand_returns200AndText() throws Exception {
        when(service.updateBrandIfNotInUse("D-002", "Apple"))
                .thenReturn("Brand updated");

        mockMvc.perform(put("/private/v1/device/updateBrand/{id}/{newBrand}",
                        "D-002", "Apple"))
                .andExpect(status().isOk())
                .andExpect(content().string("Brand updated"));

        verify(service).updateBrandIfNotInUse("D-002", "Apple");
    }

    @Test
    void getById_returns200AndBody() throws Exception {
        Device dev = Device.builder()
                .id("XYZ").name("Router").brand("Cisco")
                .state(DeviceState.IN_USE).creationTime(LocalDateTime.now()).build();

        when(aggregator.getDeviceById("XYZ")).thenReturn(dev);

        mockMvc.perform(get("/private/v1/device/fetch/{id}", "XYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Cisco"));

        verify(aggregator).getDeviceById("XYZ");
    }


    @Test
    void getAll_noParams_callsAggregatorWithNulls() throws Exception {
        when(aggregator.fetchDevices(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/private/v1/device/fetch"))
                .andExpect(status().isOk());

        verify(aggregator).fetchDevices(null, null);
    }

    @Test
    void getAll_brandOnly() throws Exception {
        when(aggregator.fetchDevices("Samsung", null)).thenReturn(List.of());

        mockMvc.perform(get("/private/v1/device/fetch").param("brand", "Samsung"))
                .andExpect(status().isOk());

        verify(aggregator).fetchDevices("Samsung", null);
    }

    @Test
    void getAll_stateOnly() throws Exception {
        when(aggregator.fetchDevices(null, "AVAILABLE")).thenReturn(List.of());

        mockMvc.perform(get("/private/v1/device/fetch").param("state", "AVAILABLE"))
                .andExpect(status().isOk());

        verify(aggregator).fetchDevices(null, "AVAILABLE");
    }

    @Test
    void getAll_brandAndState() throws Exception {
        when(aggregator.fetchDevices("Dell", "IN_USE")).thenReturn(List.of());

        mockMvc.perform(get("/private/v1/device/fetch")
                        .param("brand", "Dell")
                        .param("state", "IN_USE"))
                .andExpect(status().isOk());

        verify(aggregator).fetchDevices("Dell", "IN_USE");
    }

    @Test
    void delete_returns200AndText() throws Exception {
        when(aggregator.deleteDevice("DEL-3")).thenReturn("deleted");

        mockMvc.perform(delete("/private/v1/device/{id}", "DEL-3"))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted"));

        verify(aggregator).deleteDevice("DEL-3");
    }
}
