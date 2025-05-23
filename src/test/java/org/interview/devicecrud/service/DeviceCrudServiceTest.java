package org.interview.devicecrud.service;

import com.mongodb.client.result.UpdateResult;
import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.exception.MongoDBException;
import org.interview.devicecrud.model.Device;
import org.interview.devicecrud.repository.DeviceCrudMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DeviceCrudServiceTest {

    @Mock
    private DeviceCrudMongoRepository repository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private DeviceCrudService service;

    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device("DEV-PIXE-GOOG-7508E2","PIXEL",
                "GOOGLE",DeviceState.AVAILABLE ,LocalDateTime.now());
    }

    @Test
    void createNewDevice_success() {
        when(repository.save(device)).thenReturn(device);

        Device created = service.createNewDevice(device);

        assertEquals(device, created);
        verify(repository).save(device);
    }

    @Test
    void createNewDevice_failure_wrapsException() {
        when(repository.save(device)).thenThrow(new RuntimeException("db down"));

        MongoDBException ex = assertThrows(MongoDBException.class,
                () -> service.createNewDevice(device));

        assertTrue(ex.getMessage().contains("Failed to create device"));
    }

    @Test
    void updateDevice_success() {
        when(repository.save(device)).thenReturn(device);

        Device updated = service.updateDevice(device.getId(), device);

        assertEquals(device, updated);
        verify(repository).save(device);
    }

    @Test
    void updateDevice_failure() {
        when(repository.save(any())).thenThrow(new RuntimeException("db error"));

        assertThrows(MongoDBException.class,
                () -> service.updateDevice("DEV-PIXE-GOOG-7508E2", device));
    }


    @Test
    void updateBrandIfNotInUse_success() {
        UpdateResult result = mock(UpdateResult.class);
        when(result.getModifiedCount()).thenReturn(1L);
        when(mongoTemplate.updateFirst(any(Query.class), any(), eq(Device.class)))
                .thenReturn(result);

        String reply = service.updateBrandIfNotInUse("DEV-PIXE-GOOG-7508E2", "Apple");

        assertEquals("Brand of the device with id: DEV-PIXE-GOOG-7508E2 updated to \"Apple\"", reply);
    }

    @Test
    void updateBrandIfNotInUse_zeroModified_throws() {
        UpdateResult result = mock(UpdateResult.class);
        when(result.getModifiedCount()).thenReturn(0L);
        when(mongoTemplate.updateFirst(any(Query.class), any(), eq(Device.class)))
                .thenReturn(result);

        assertThrows(MongoDBException.class,
                () -> service.updateBrandIfNotInUse("DEV-PIXE-GOOG-7508E2", "Dell"));
    }

    @Test
    void fetchDeviceById_found() {
        when(repository.findById("DEV-PIXE-GOOG-7508E2")).thenReturn(Optional.of(device));

        Device found = service.fetchDeviceById("DEV-PIXE-GOOG-7508E2");

        assertEquals(device, found);
    }

    @Test
    void fetchDeviceById_notFound() {
        when(repository.findById("DEV-PIXE-GOOG-7508E2")).thenReturn(Optional.empty());

        assertThrows(MongoDBException.class,
                () -> service.fetchDeviceById("DEV-PIXE-GOOG-7508E2"));
    }

    @Test
    void getAllDevices_success() {
        when(repository.findAll()).thenReturn(List.of(device));

        List<Device> devices = service.getAllDevices();

        assertEquals(1, devices.size());
        verify(repository).findAll();
    }

    @Test
    void deviceExists_true() {
        when(repository.existsById("DEV-PIXE-GOOG-7508E2")).thenReturn(true);

        assertTrue(service.deviceExists("DEV-PIXE-GOOG-7508E2"));
    }

    @Test
    void getDevicesByBrand_success() {
        when(repository.findByBrand("Google")).thenReturn(List.of(device));

        List<Device> result = service.getDevicesByBrand("Google");

        assertEquals(1, result.size());
    }

    @Test
    void getDevicesByState_success() {
        when(repository.findByState(DeviceState.AVAILABLE))
                .thenReturn(List.of(device));

        List<Device> result = service.getDevicesByState(DeviceState.AVAILABLE);

        assertEquals(1, result.size());
    }

    @Test
    void getDevicesByBrandAndState_success() {
        when(mongoTemplate.find(any(Query.class), eq(Device.class)))
                .thenReturn(List.of(device));

        List<Device> result = service.getDevicesByBrandAndState("Google",
                DeviceState.AVAILABLE);

        assertEquals(1, result.size());

        // Optional –- make sure the query contained both criteria:
        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).find(captor.capture(), eq(Device.class));
        String q = captor.getValue().toString();
        assertTrue(q.contains("brand"));
        assertTrue(q.contains("state"));
    }


    @Test
    void deleteDevice_success() {
        doNothing().when(repository).deleteById("DEV-PIXE-GOOG-7508E2");

        String msg = service.deleteDevice("DEV-PIXE-GOOG-7508E2");

        assertEquals("Device with id: DEV-PIXE-GOOG-7508E2 deleted", msg);
        verify(repository).deleteById("DEV-PIXE-GOOG-7508E2");
    }
}
