package org.interview.devicecrud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.interview.devicecrud.aggregator.DeviceCrudAggregator;
import org.interview.devicecrud.model.Device;
import org.interview.devicecrud.model.DeviceCreationRequest;
import org.interview.devicecrud.model.DeviceUpdationRequest;
import org.interview.devicecrud.service.DeviceCrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/v1/device")
@Tag(name = "Devices", description = "This API facilitates CRUD operation on Devices")
public class DeviceCrudController {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCrudController.class);

    private final DeviceCrudAggregator deviceAggregator;
    private final DeviceCrudService deviceService;

    public DeviceCrudController(DeviceCrudAggregator deviceAggregator, DeviceCrudService deviceService) {
        this.deviceAggregator = deviceAggregator;
        this.deviceService = deviceService;
    }

    @Operation(summary = "Create a device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Device.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"id\":\"123\",\"brand\":\"Apple\",\"model\":\"iPhone 14\",\"state\":\"NEW\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid device creation request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid device creation data\"}")
                    )),
            @ApiResponse(responseCode = "409", description = "Duplicate device creation attempt",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":409,\"error\":\"Conflict\",\"message\":\"Device already exists\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error during device creation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Database error occurred\"}")
                    ))
    })
    @PostMapping("/create")
    public ResponseEntity<Device> create(@Valid @RequestBody DeviceCreationRequest deviceRequest) {
        logger.debug("Received request to create device: {}", deviceRequest);
        Device createdDevice = deviceAggregator.createDevice(deviceRequest);
        logger.info("Device created with ID: {}", createdDevice.getId());
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Device.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"id\":\"123\",\"brand\":\"Apple\",\"model\":\"iPhone 14 Pro\",\"state\":\"USED\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid device update request or invalid device ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid device ID format\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error during device update",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Database update failed\"}")
                    ))
    })
    @PutMapping("update/{id}")
    public ResponseEntity<Device> update(@PathVariable String id, @Valid @RequestBody DeviceUpdationRequest updateRequest) {
        logger.debug("Received request to update device: {}", updateRequest);
        Device updatedDevice = deviceAggregator.updateDevice(id, updateRequest);
        logger.info("Device with ID: {} has been updated.", updatedDevice.getId());
        return ResponseEntity.ok(updatedDevice);
    }

    @Operation(summary = "Update brand of a device if it is not in use")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "\"Brand updated successfully\"")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request or device is in use",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Device is currently in use and brand cannot be updated\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error during brand update",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Database error occurred\"}")
                    ))
    })
    @PutMapping("updateBrand/{id}/{newBrand}")
    public ResponseEntity<String> update(@PathVariable String id, @PathVariable String newBrand) {
        logger.debug("Received request to update brand for device id: {} to new brand: {}", id, newBrand);
        String response = deviceService.updateBrandIfNotInUse(id, newBrand);
        logger.info("Brand updated to {} for device id {}", newBrand, id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Fetch a device by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device fetched successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Device.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"id\":\"123\",\"brand\":\"Apple\",\"model\":\"iPhone 14\",\"state\":\"NEW\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid device ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid device ID format\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error during fetch",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Database error occurred\"}")
                    ))
    })
    @GetMapping("/fetch/{id}")
    public ResponseEntity<Device> get(@PathVariable String id) {
        logger.debug("Start fetch device details for id : {}", id);
        Device device = deviceAggregator.getDeviceById(id);
        logger.info("Fetched device with id: {}", device.getId());
        return ResponseEntity.ok(device);
    }

    @Operation(summary = "Fetch all devices with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devices fetched successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Device.class, type = "array"),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "[{\"id\":\"123\",\"brand\":\"Apple\",\"model\":\"iPhone 14\",\"state\":\"NEW\"},{\"id\":\"124\",\"brand\":\"Samsung\",\"model\":\"Galaxy S21\",\"state\":\"USED\"}]")
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error during fetch",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Database error occurred\"}")
                    ))
    })
    @GetMapping("/fetch")
    public ResponseEntity<List<Device>> getAll(@RequestParam(required = false) String brand,
                                               @RequestParam(required = false) String state) {
        logger.debug("Fetching devices{}",
                (brand != null && state != null) ? String.format(" with brand='%s' and state='%s'", brand, state)
                        : (brand != null) ? String.format(" with brand='%s'", brand)
                        : (state != null) ? String.format(" with state='%s'", state)
                        : " : ALL");
        List<Device> deviceList = deviceAggregator.fetchDevices(brand, state);
        logger.info("Fetched {} devices", deviceList.size());
        return ResponseEntity.ok(deviceList);
    }

    @Operation(summary = "Delete a device by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "\"Device deleted successfully\"")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid deletion request or device ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid device ID format\"}")
                    )),
            @ApiResponse(responseCode = "500", description = "Internal server error during deletion",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"timestamp\":\"2025-05-25T12:34:56\",\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Database deletion error\"}")
                    ))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        logger.info("Received delete request for device id: {}", id);
        String response = deviceAggregator.deleteDevice(id);
        logger.info("Device deleted with id {} ", id);
        return ResponseEntity.ok(response);
    }
}
