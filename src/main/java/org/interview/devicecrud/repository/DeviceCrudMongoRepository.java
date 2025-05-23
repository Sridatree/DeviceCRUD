package org.interview.devicecrud.repository;

import java.util.List;

import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceCrudMongoRepository extends MongoRepository<Device, String> {

    List<Device> findByBrand(String brand);
    List<Device> findByState(DeviceState state);
}
