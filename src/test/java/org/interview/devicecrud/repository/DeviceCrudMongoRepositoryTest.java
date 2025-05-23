package org.interview.devicecrud.repository;

import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.model.Device;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.mongodb.port=0" // random port for embedded mongo
})
@AutoConfigureDataMongo
public class DeviceCrudMongoRepositoryTest {

    @Autowired
    private DeviceCrudMongoRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Find devices by brand")
    void testFindByBrand() {

        Device device1 = new Device("1","Iphone14" ,"Apple", DeviceState.AVAILABLE, LocalDateTime.now());
        Device device2 = new Device("2","S24Ultra" ,"Samsung", DeviceState.IN_USE, LocalDateTime.now());
        Device device3 = new Device("3", "Iphone13","Apple", DeviceState.IN_USE, LocalDateTime.now());
        repository.save(device1);
        repository.save(device2);
        repository.save(device3);

        List<Device> appleDevices = repository.findByBrand("Apple");

        assertThat(appleDevices).hasSize(2);
        assertThat(appleDevices).extracting(Device::getBrand).allMatch(brand -> brand.equals("Apple"));
    }

    @Test
    @DisplayName("Find devices by state")
    void testFindByState() {

        Device device1 = new Device("1","Iphone14" ,"Apple", DeviceState.AVAILABLE, LocalDateTime.now());
        Device device2 = new Device("2","S24Ultra" ,"Samsung", DeviceState.IN_USE, LocalDateTime.now());
        Device device3 = new Device("3", "Iphone13","Apple", DeviceState.IN_USE, LocalDateTime.now());

        repository.save(device1);
        repository.save(device2);
        repository.save(device3);

        List<Device> availableDevices = repository.findByState(DeviceState.AVAILABLE);

        assertThat(availableDevices).hasSize(2);
        assertThat(availableDevices).extracting(Device::getState).allMatch(state -> state == DeviceState.AVAILABLE);
    }
}
