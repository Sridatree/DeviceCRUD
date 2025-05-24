package org.interview.devicecrud.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.interview.devicecrud.constants.DeviceState;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceCreationRequest {
    private String name;
    private String brand;
    private String state;
    private LocalDateTime creationTime;
}
