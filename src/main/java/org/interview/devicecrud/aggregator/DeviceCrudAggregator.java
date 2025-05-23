package org.interview.devicecrud.aggregator;

import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.exception.DuplicateDeviceException;
import org.interview.devicecrud.exception.InvalidDeletionException;
import org.interview.devicecrud.exception.InvalidDeviceIdException;
import org.interview.devicecrud.model.Device;
import org.interview.devicecrud.model.DeviceCreationRequest;
import org.interview.devicecrud.model.DeviceUpdationRequest;
import org.interview.devicecrud.service.DeviceCrudService;
import org.interview.devicecrud.service.DeviceIdGenerator;
import org.interview.devicecrud.validator.DeviceCrudValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class DeviceCrudAggregator {

    private static final Logger logger = LoggerFactory.getLogger(DeviceCrudAggregator.class);

    private final DeviceCrudService deviceService;
    private final DeviceIdGenerator deviceIdGenerator;
    private final DeviceCrudValidator validator;

    private DeviceCrudAggregator(DeviceCrudService deviceService,
                                 DeviceIdGenerator deviceIdGenerator,
                                 DeviceCrudValidator validator) {
        this.deviceService = deviceService;
        this.deviceIdGenerator = deviceIdGenerator;
        this.validator = validator;
    }

    /**
     * This method is used to validate the device creation request and then
     * proceed towards device creation upon successful validation
     *
     * @param deviceRequest Device creation request received
     * @return The device created
     *
     */
    public Device createDevice(DeviceCreationRequest deviceRequest){
        validator.validateDeviceCreateRequest(deviceRequest);
        //Generate unique device id
        String deviceId = deviceIdGenerator.generateId(deviceRequest.getName(),
                deviceRequest.getBrand());
        //check if id already exists in db
        if (deviceService.deviceExists(deviceId)) {
            throw new DuplicateDeviceException("Device with same name and brand already exists.");
        }
        Device device = Device.builder().id(deviceId).name(deviceRequest.getName())
                .brand(deviceRequest.getBrand()).state(DeviceState.fromString(deviceRequest.getState()))
                .creationTime(deviceRequest.getCreationTime()).build();
        return deviceService.createNewDevice(device);
    }

    /**
     * This method is used to validate the input update request and
     * update the device if the device is not in use and if the validation
     * is successful
     * @param id id of the request to be updated
     * @param updateRequest request received for update
     * @return the updated device
     */
    public Device updateDevice(String id, DeviceUpdationRequest updateRequest) {

        if (!StringUtils.hasText(id)) {
            logger.error("Device id is empty");
            throw new InvalidDeviceIdException("Device ID must not be blank.", id);
        }
        Device existingDevice = deviceService.fetchDeviceById(id);
        validator.validateDeviceUpdateRequest(updateRequest, existingDevice);
        Device updatedDevice = Device.builder().id(existingDevice.getId()).name(updateRequest.getName())
                .brand(updateRequest.getBrand()).state(DeviceState.fromString(updateRequest.getState()))
                .creationTime(existingDevice.getCreationTime()).build();
        return deviceService.updateDevice(id, updatedDevice);
    }

    /**
     * Method to fetch a device on basis of the id
     * @param id id of the device to be fetched
     * @return the device
     */
    public Device getDeviceById(String id) {
        validator.validateId(id);
        return deviceService.fetchDeviceById(id);
    }

    /**
     * This method is used to fetch all devices, devices of a
     * particular brand or state or both
     * @param brand  The brand of devices to be fetched
     * @param state  The state of devices to be fetched
     * @return list of devices
     */
    public List<Device> fetchDevices(String brand, String state){
        if (StringUtils.hasText(state) && !DeviceState.isValidState(state)) {
            throw new IllegalArgumentException("Invalid or blank device state. Must be one of: AVAILABLE, IN-USE, INACTIVE");
        }
        DeviceState deviceState = DeviceState.fromString(state);
        return switch ((brand != null ? 1 : 0) + (state != null ? 2 : 0)) {
            case 3 -> deviceService.getDevicesByBrandAndState(brand, deviceState); // both present
            case 1 -> deviceService.getDevicesByBrand(brand);                // only brand
            case 2 -> deviceService.getDevicesByState(deviceState);                // only state
            default -> deviceService.getAllDevices();                        // none
        };
    }

    /**
     * Delete a device by its id
     * @param id id of the device to be deleted
     * @return Response message stating device has been deleted
     */
    public String deleteDevice(String id) {
        if (!StringUtils.hasText(id)) {
            logger.error("Device id to delete is empty");
            throw new InvalidDeviceIdException("Device ID for deletion must not be blank.", id);
        }
        Device existingDevice = deviceService.fetchDeviceById(id);
        if (existingDevice.getState() == DeviceState.IN_USE) {
            throw new InvalidDeletionException("Cannot delete device that is in use");
        }
        return deviceService.deleteDevice(id);
    }

}
