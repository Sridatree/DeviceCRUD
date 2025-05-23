package org.interview.devicecrud.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.interview.devicecrud.constants.DeviceState;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class DeviceCreationRequest {
    private String name;
    private String brand;
    private String state;
    private LocalDateTime creationTime;
}
