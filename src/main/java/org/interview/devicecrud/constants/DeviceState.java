package org.interview.devicecrud.constants;

public enum DeviceState {
    AVAILABLE("AVAILABLE"),
    IN_USE("IN_USE"),
    INACTIVE("INACTIVE"),
    INVALID("INVALID");  // New invalid state

    private final String value;

    DeviceState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValidState(String state) {
        for (DeviceState ds : DeviceState.values()) {
            if (ds.getValue().equalsIgnoreCase(state) && ds != INVALID) {
                return true;
            }
        }
        return false;
    }

    public static DeviceState fromString(String state) {
        for (DeviceState ds : DeviceState.values()) {
            if (ds.getValue().equalsIgnoreCase(state)) {
                return ds;
            }
        }
        return INVALID;
    }
}
