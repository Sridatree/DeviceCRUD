package org.interview.devicecrud.repository;

import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.model.Device;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class DeviceCrudMongoRepositoryTest {

    @Mock
    private DeviceCrudMongoRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByBrand() {

        Device device1 = new Device("1", "Iphone14", "Apple", DeviceState.AVAILABLE, LocalDateTime.now());
        Device device2 = new Device("3", "Iphone13", "Apple", DeviceState.IN_USE, LocalDateTime.now());
        List<Device> appleDevices = Arrays.asList(device1, device2);

        when(repository.findByBrand("Apple")).thenReturn(appleDevices);

        List<Device> result = repository.findByBrand("Apple");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Device::getBrand).allMatch(brand -> brand.equals("Apple"));
    }

    @Test
    void testFindByState() {
        Device device = new Device("1", "Iphone14", "Apple", DeviceState.AVAILABLE, LocalDateTime.now());
        List<Device> availableDevices = List.of(device);

        when(repository.findByState(DeviceState.AVAILABLE)).thenReturn(availableDevices);

        List<Device> result = repository.findByState(DeviceState.AVAILABLE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getState()).isEqualTo(DeviceState.AVAILABLE);
    }
}
