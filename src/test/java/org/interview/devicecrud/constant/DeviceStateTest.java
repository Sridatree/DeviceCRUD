package org.interview.devicecrud.constant;

import org.interview.devicecrud.constants.DeviceState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeviceStateTest {

    @Test
    @DisplayName("Should return true for valid states")
    void testIsValidState_validStates() {
        assertThat(DeviceState.isValidState("AVAILABLE")).isTrue();
        assertThat(DeviceState.isValidState("IN_USE")).isTrue();
        assertThat(DeviceState.isValidState("INACTIVE")).isTrue();
    }

    @Test
    @DisplayName("Should return false for invalid or UNKNOWN state")
    void testIsValidState_invalidStates() {
        assertThat(DeviceState.isValidState("INVALID")).isFalse(); // explicitly excluded
        assertThat(DeviceState.isValidState("UNKNOWN")).isFalse();
        assertThat(DeviceState.isValidState("")).isFalse();
        assertThat(DeviceState.isValidState(null)).isFalse();
    }

    @Test
    @DisplayName("Should convert string to DeviceState enum")
    void testFromString_validConversions() {
        assertThat(DeviceState.fromString("AVAILABLE")).isEqualTo(DeviceState.AVAILABLE);
        assertThat(DeviceState.fromString("in_use")).isEqualTo(DeviceState.IN_USE); // case insensitive
        assertThat(DeviceState.fromString("InAcTiVe")).isEqualTo(DeviceState.INACTIVE);
    }

    @Test
    @DisplayName("Should return INVALID enum for unknown input")
    void testFromString_invalidConversion() {
        assertThat(DeviceState.fromString("RANDOM")).isEqualTo(DeviceState.INVALID);
        assertThat(DeviceState.fromString(null)).isEqualTo(DeviceState.INVALID);
        assertThat(DeviceState.fromString("")).isEqualTo(DeviceState.INVALID);
    }

    @Test
    @DisplayName("Should return correct string value from getValue()")
    void testGetValue() {
        assertThat(DeviceState.AVAILABLE.getValue()).isEqualTo("AVAILABLE");
        assertThat(DeviceState.IN_USE.getValue()).isEqualTo("IN_USE");
        assertThat(DeviceState.INACTIVE.getValue()).isEqualTo("INACTIVE");
        assertThat(DeviceState.INVALID.getValue()).isEqualTo("INVALID");
    }
}
