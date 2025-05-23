package org.interview.devicecrud.validator;

import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.exception.InvalidDeviceCreationException;
import org.interview.devicecrud.exception.InvalidDeviceIdException;
import org.interview.devicecrud.exception.InvalidDeviceUpdateException;
import org.interview.devicecrud.model.Device;
import org.interview.devicecrud.model.DeviceCreationRequest;
import org.interview.devicecrud.model.DeviceUpdationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;

@Component
public class DeviceCrudValidator {

    public void validateDeviceCreateRequest(DeviceCreationRequest request) {
        isNameValid(request.getName());
        isBrandValid(request.getBrand());
        isStateValid(request.getState());
        if (request.getCreationTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Creation time cannot be in the future");
        }
    }

    public void validateDeviceUpdateRequest(DeviceUpdationRequest request, Device existingDevice) {
        isNameValid(request.getName());
        isBrandValid(request.getBrand());
        isStateValid(request.getState());
        if (existingDevice.getState() == DeviceState.IN_USE) {
            throw new InvalidDeviceUpdateException("Cannot update name or brand of a device that is in use");
        }
    }

    public void validateId(String id){
        if (!StringUtils.hasText(id)) {
            throw new InvalidDeviceIdException("Please enter a valid device id", id);
        }
    }

    public void isNameValid(String name){
        if(!StringUtils.hasText(name)){
            throw new InvalidDeviceCreationException("Device name cannot be empty.Please enter a valid name.");
        }
    }

    public void isBrandValid(String brand){
        if(!StringUtils.hasText(brand)){
            throw new InvalidDeviceCreationException("Device brand cannot be empty.Please enter a valid brand.");
        }
    }

    public void isStateValid(String state){
        if (!StringUtils.hasText(state) || !DeviceState.isValidState(state)) {
            throw new IllegalArgumentException("Invalid or blank device state. Must be one of: AVAILABLE, IN-USE, INACTIVE");
        }
    }
}
