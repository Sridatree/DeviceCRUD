package org.interview.devicecrud.controller;

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
public class DeviceCrudController {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCrudController.class);

    private final DeviceCrudAggregator deviceAggregator;
    private final DeviceCrudService deviceService;

    public DeviceCrudController(DeviceCrudAggregator deviceAggregator, DeviceCrudService deviceService) {
        this.deviceAggregator = deviceAggregator;
        this.deviceService = deviceService;
    }

    /**
     * Creates a new device in the system.
     *
     * @param deviceRequest the request body containing device details
     * @return the created {@link Device} object
     */

    @PostMapping("/create")
    public ResponseEntity<Device> create(@Valid @RequestBody DeviceCreationRequest deviceRequest){
        logger.debug("Received request to create device: {}", deviceRequest);
        Device createdDevice = deviceAggregator.createDevice(deviceRequest);
        logger.info("Device created with ID: {}", createdDevice.getId());
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    /**
     *Update a device based on the updated request passed
     * @param id of the device required to be updated
     * @param updateRequest the request body containing the device details to be updated to
     * @return the updated {@link Device} object
     */

    @PutMapping("update/{id}")
    public ResponseEntity<Device> update(@PathVariable String id, @Valid @RequestBody DeviceUpdationRequest updateRequest) {
        logger.debug("Received request to update device: {}", updateRequest);
        Device updatedDevice = deviceAggregator.updateDevice(id, updateRequest);
        logger.info("Device with ID: {} has been updated.", updatedDevice.getId());
        return ResponseEntity.ok(updatedDevice);
    }

    /**
     * Update brand of a device of passed id iff the device is not in use
     * @param id of the device whose brand needs to be updated
     * @param newBrand the brand to be updated to
     * @return A completion text that the brand has been updated.
     */
    @PutMapping("updateBrand/{id}/{newBrand}")
    public ResponseEntity<String> update(@PathVariable String id, @PathVariable String newBrand) {
        logger.debug("Received request to update brand for device id: {} to new brand: {}", id, newBrand);
        String response = deviceService.updateBrandIfNotInUse(id, newBrand);
        logger.info("Brand updated to {} for device id {}", newBrand, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetch a device of given id
     * @param id The id of the device to be fetched
     * @return The Device information
     */
    @GetMapping("/fetch/{id}")
    public ResponseEntity<Device> get(@PathVariable String id) {
        logger.debug("Start fetch device details for id : {}", id);
        Device device = deviceAggregator.getDeviceById(id);
        logger.info("Fetched device with id: {}", device.getId());
        return ResponseEntity.ok(device);
    }

    /**
     * Fetch all devices from the database if no param is provided
     * if Brand is provided in request param, fetch all devices of a particular brand
     * if Device state is provided in request param, fetch all devices of a particular state
     * if both Brand and state are passed in request param, fetch all devices of that state and brand
     * @param brand Brand of the devices that needs to be fetched
     * @param state State of the devices that needs to be fetched
     * @return list of all devices fetched
     */

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

    /**
     * Delete the device with the given id
     * @param id id of the request to be deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        logger.info("Received delete request for device id: {}", id);
        String response = deviceAggregator.deleteDevice(id);
        logger.info("Device deleted with id {} ", id);
        return ResponseEntity.ok(response);
    }
}
