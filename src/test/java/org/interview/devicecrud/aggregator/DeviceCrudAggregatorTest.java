package org.interview.devicecrud.aggregator;

import org.interview.devicecrud.constants.DeviceState;
import org.interview.devicecrud.exception.*;
import org.interview.devicecrud.model.*;
import org.interview.devicecrud.service.DeviceCrudService;
import org.interview.devicecrud.service.DeviceIdGenerator;
import org.interview.devicecrud.validator.DeviceCrudValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pure-unit tests for {@link DeviceCrudAggregator}.
 */
@ExtendWith(MockitoExtension.class)
class DeviceCrudAggregatorTest {

    @Mock DeviceCrudService deviceService;
    @Mock DeviceIdGenerator deviceIdGenerator;
    @Mock DeviceCrudValidator validator;

    private DeviceCrudAggregator aggregator;

    private final LocalDateTime now = LocalDateTime.of(2025, 1, 1, 10, 0);

    @BeforeEach
    void init() throws Exception {
        // the constructor in the component is private – use reflection
        Constructor<DeviceCrudAggregator> c =
                DeviceCrudAggregator.class.getDeclaredConstructor(
                        DeviceCrudService.class,
                        DeviceIdGenerator.class,
                        DeviceCrudValidator.class);
        c.setAccessible(true);
        aggregator = c.newInstance(deviceService, deviceIdGenerator, validator);
    }

    /* ------------------------------------------------------------------
     * createDevice
     * ------------------------------------------------------------------ */
    @Test
    void createDevice_success() {
        DeviceCreationRequest req = DeviceCreationRequest.builder()
                .name("Galaxy S24")
                .brand("Samsung")
                .state("AVAILABLE")
                .creationTime(now)
                .build();

        Device created = Device.builder()
                .id("SAMSUNG_GALAXY_S24")
                .name("Galaxy S24")
                .brand("Samsung")
                .state(DeviceState.AVAILABLE)
                .creationTime(now)
                .build();

        when(deviceIdGenerator.generateId(req.getName(), req.getBrand()))
                .thenReturn("SAMSUNG_GALAXY_S24");
        when(deviceService.deviceExists("SAMSUNG_GALAXY_S24")).thenReturn(false);
        when(deviceService.createNewDevice(any(Device.class))).thenReturn(created);

        Device result = aggregator.createDevice(req);

        assertThat(result).isEqualTo(created);

        verify(validator).validateDeviceCreateRequest(req);
        // ensure the Device passed to service carries the generated id
        ArgumentCaptor<Device> captor = ArgumentCaptor.forClass(Device.class);
        verify(deviceService).createNewDevice(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("SAMSUNG_GALAXY_S24");
    }

    @Test
    void createDevice_whenDuplicate_throwsDuplicateDeviceException() {
        DeviceCreationRequest req = DeviceCreationRequest.builder()
                .name("iPhone 15")
                .brand("Apple")
                .state("AVAILABLE")
                .creationTime(now)
                .build();

        when(deviceIdGenerator.generateId("iPhone 15", "Apple"))
                .thenReturn("APPLE_IPHONE_15");
        when(deviceService.deviceExists("APPLE_IPHONE_15")).thenReturn(true);

        assertThrows(DuplicateDeviceException.class,
                () -> aggregator.createDevice(req));

        verify(deviceService, never()).createNewDevice(any());
    }

    /* ------------------------------------------------------------------
     * updateDevice
     * ------------------------------------------------------------------ */
    @Test
    void updateDevice_success() {
        String id = "D-001";
        Device existing = Device.builder()
                .id(id).name("XPS 13").brand("Dell")
                .state(DeviceState.AVAILABLE).creationTime(now).build();

        DeviceUpdationRequest updReq = DeviceUpdationRequest.builder()
                .name("XPS 14")
                .brand("Dell")
                .state("AVAILABLE")
                .build();

        Device updated = Device.builder()
                .id(id).name("XPS 14").brand("Dell")
                .state(DeviceState.AVAILABLE).creationTime(now).build();

        when(deviceService.fetchDeviceById(id)).thenReturn(existing);
        when(deviceService.updateDevice(eq(id), any(Device.class))).thenReturn(updated);

        Device result = aggregator.updateDevice(id, updReq);

        assertThat(result).isEqualTo(updated);
        verify(validator).validateDeviceUpdateRequest(updReq, existing);
    }

    @Test
    void updateDevice_blankId_throwsInvalidDeviceId() {
        DeviceUpdationRequest updReq = DeviceUpdationRequest.builder()
                .name("x").brand("y").state("AVAILABLE").build();

        assertThrows(InvalidDeviceIdException.class,
                () -> aggregator.updateDevice("  ", updReq));
    }

    /* ------------------------------------------------------------------
     * getDeviceById
     * ------------------------------------------------------------------ */
    @Test
    void getDeviceById_delegatesToServiceAndValidator() {
        Device dev = Device.builder().id("X").name("Any").brand("Any")
                .state(DeviceState.AVAILABLE).creationTime(now).build();

        when(deviceService.fetchDeviceById("X")).thenReturn(dev);

        Device result = aggregator.getDeviceById("X");

        assertThat(result).isEqualTo(dev);
        verify(validator).validateId("X");
    }

    /* ------------------------------------------------------------------
     * fetchDevices
     * ------------------------------------------------------------------ */
    @Test
    void fetchDevices_noFilters_callsGetAll() {
        when(deviceService.getAllDevices()).thenReturn(List.of());

        aggregator.fetchDevices(null, null);

        verify(deviceService).getAllDevices();
    }

    @Test
    void fetchDevices_brandOnly_callsGetByBrand() {
        aggregator.fetchDevices("Samsung", null);

        verify(deviceService).getDevicesByBrand("Samsung");
    }

    @Test
    void fetchDevices_stateOnly_callsGetByState() {
        aggregator.fetchDevices(null, "AVAILABLE");

        verify(deviceService).getDevicesByState(DeviceState.AVAILABLE);
    }

    @Test
    void fetchDevices_brandAndState_callsGetByBrandAndState() {
        aggregator.fetchDevices("Dell", "IN_USE");

        verify(deviceService).getDevicesByBrandAndState("Dell", DeviceState.IN_USE);
    }

    @Test
    void fetchDevices_invalidState_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> aggregator.fetchDevices(null, "WRONG"));
    }

    /* ------------------------------------------------------------------
     * deleteDevice
     * ------------------------------------------------------------------ */
    @Test
    void deleteDevice_success() {
        String id = "DEL-1";
        Device available = Device.builder().id(id).name("Router")
                .brand("Cisco").state(DeviceState.AVAILABLE).creationTime(now).build();

        when(deviceService.fetchDeviceById(id)).thenReturn(available);
        when(deviceService.deleteDevice(id)).thenReturn("deleted");

        String msg = aggregator.deleteDevice(id);

        assertThat(msg).isEqualTo("deleted");
        verify(deviceService).deleteDevice(id);
    }

    @Test
    void deleteDevice_whenInUse_throwsInvalidDeletion() {
        String id = "INUSE-1";
        Device inUse = Device.builder().id(id).name("Modem")
                .brand("Netgear").state(DeviceState.IN_USE).creationTime(now).build();

        when(deviceService.fetchDeviceById(id)).thenReturn(inUse);

        assertThrows(InvalidDeletionException.class, () -> aggregator.deleteDevice(id));

        verify(deviceService, never()).deleteDevice(anyString());
    }

    @Test
    void deleteDevice_blankId_throwsInvalidDeviceId() {
        assertThrows(InvalidDeviceIdException.class,
                () -> aggregator.deleteDevice(" "));
    }
}
