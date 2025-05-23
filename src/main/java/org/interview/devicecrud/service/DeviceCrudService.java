package org.interview.devicecrud.service;

import com.mongodb.client.result.UpdateResult;
import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.exception.MongoDBException;
import org.interview.devicecrud.model.Device;
import org.interview.devicecrud.repository.DeviceCrudMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class DeviceCrudService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCrudService.class);
    private final DeviceCrudMongoRepository repository;
    private final MongoTemplate mongoTemplate;

    public DeviceCrudService(DeviceCrudMongoRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public Device createNewDevice(Device device) {
        try {
            logger.debug("Creating new device : {}", device);
            return repository.save(device);
        } catch (Exception e) {
            logger.error("Error while creating device: {}", e.getMessage());
            throw new MongoDBException( "Failed to create device : ", e.getMessage());
        }
    }

    public Device updateDevice(String id, Device update) {
        try {
            return repository.save(update);
        } catch (Exception e) {
            logger.error("Error updating device with id {}: {}", id, e.getMessage(), e);
            throw new MongoDBException( "Failed to update device", e.getMessage());
        }
    }

    public String updateBrandIfNotInUse(String deviceId, String newBrand) {
        try {
            Query query = new Query(Criteria.where("id").is(deviceId).and("state").ne("IN_USE"));
            Update update = new Update().set("brand", newBrand);
            UpdateResult result = mongoTemplate.updateFirst(query, update, Device.class);

            if (result.getModifiedCount() == 0) {
                throw new MongoDBException("Device is either in use or not found.", deviceId);
            }

            return String.format("Brand of the device with id: %s updated to \"%s\"", deviceId, newBrand);
        } catch (Exception e) {
            logger.error("Error updating brand: {}", e.getMessage(), e);
            throw new MongoDBException("Failed to update brand", e.getMessage());
        }
    }

    public Device fetchDeviceById(String id) {
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new MongoDBException("Device not found with id: ",id));
        }catch (Exception e) {
            if (e instanceof MongoDBException mongoEx) throw mongoEx;
            logger.error("Unexpected error fetching device with id {}: {}", id, e.getMessage(), e);
            throw new MongoDBException("Failed to fetch device ", e.getMessage());
        }
    }

    public List<Device> getAllDevices() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all devices: {}", e.getMessage(), e);
            throw new MongoDBException("Failed to fetch devices", e.getMessage());
        }
    }

    public boolean deviceExists(String id) {
        try {
            return repository.existsById(id);
        } catch (Exception e) {
            logger.error("Error checking device existence: {}", e.getMessage(), e);
            throw new MongoDBException("Failed to verify device existence: ", e.getMessage());
        }
    }

    public List<Device> getDevicesByBrand(String brand) {
        try {
            return repository.findByBrand(brand);
        } catch (Exception e) {
            logger.error("Error fetching devices by brand: {}", e.getMessage(), e);
            throw new MongoDBException( "Failed to fetch devices by brand: ", e.getMessage());
        }
    }

    public List<Device> getDevicesByState(DeviceState state) {
        try {
            return repository.findByState(state);
        } catch (Exception e) {
            logger.error("Error fetching devices by state: {}", e.getMessage(), e);
            throw new MongoDBException("Failed to fetch devices by state: ",e.getMessage());
        }
    }

    public List<Device> getDevicesByBrandAndState(String brand, DeviceState state) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("brand").is(brand).and("state").is(state));
            return mongoTemplate.find(query, Device.class);
        } catch (Exception e) {
            logger.error("Error fetching devices by brand and state: {}", e.getMessage(), e);
            throw new MongoDBException("Failed to fetch devices by brand and state: ", e.getMessage());
        }
    }

    public String deleteDevice(String id) {
        try {
            repository.deleteById(id);
            return String.format("Device with id: %s deleted", id);
        } catch (Exception e) {
            logger.error("Error deleting device: {}", e.getMessage(), e);
            throw new MongoDBException("Failed to delete device: ", e.getMessage());
        }
    }
}
