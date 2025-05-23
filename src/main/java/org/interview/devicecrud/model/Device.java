package org.interview.devicecrud.model;


import lombok.*;

import java.time.LocalDateTime;

import org.interview.devicecrud.constants.DeviceCrudConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.interview.devicecrud.constants.DeviceState;

@Data
@AllArgsConstructor
@Builder
@ToString

@Document(collection = "device")
public class Device {

     @Id
     private String id;

     private String name;

     private String brand;

     private DeviceState state;

     private LocalDateTime creationTime;


}

