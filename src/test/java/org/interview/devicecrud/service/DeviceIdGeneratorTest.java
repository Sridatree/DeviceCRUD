package org.interview.devicecrud.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class DeviceIdGeneratorTest {

    @Mock
    private ConfigService configService;

    private DeviceIdGenerator deviceIdGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deviceIdGenerator = new DeviceIdGenerator(configService);
    }

    @Test
    @DisplayName("Should generate device ID with correct format")
    void testGenerateId_basic() {
        String name = "Iphone14";
        String brand = "Apple";
        String expectedEnv = "DEV";

        when(configService.getEnv()).thenReturn(expectedEnv);

        String deviceId = deviceIdGenerator.generateId(name, brand);

        assertThat(deviceId).startsWith("DEV-IPHO-APPL-");
        assertThat(deviceId).hasSize(20);
    }

    @Test
    @DisplayName("Should handle null values in name or brand")
    void testGenerateId_withNulls() {
        when(configService.getEnv()).thenReturn("QA");

        String deviceId = deviceIdGenerator.generateId(null, null);

        assertThat(deviceId).startsWith("QA-UNK-UNK-");
        assertThat(deviceId).hasSize(17); // e.g., QA-UNK-UNK-XXXXXX
    }

    @Test
    @DisplayName("Should remove special characters and uppercase the segments")
    void testGenerateId_specialCharacters() {
        when(configService.getEnv()).thenReturn("STG");

        String deviceId = deviceIdGenerator.generateId("I@phone#14!", "App$le*");

        // Then
        assertThat(deviceId).startsWith("STG-IPHO-APPL-");
        assertThat(deviceId).hasSize(20);
    }

    @Test
    @DisplayName("Should produce deterministic hash for same name-brand pair")
    void testGenerateId_hashConsistency() {
        when(configService.getEnv()).thenReturn("UAT");

        String id1 = deviceIdGenerator.generateId("DeviceX", "BrandY");
        String id2 = deviceIdGenerator.generateId("DeviceX", "BrandY");

        assertThat(id1).isEqualTo(id2);
    }

    @Test
    @DisplayName("Should vary hash for different name-brand combinations")
    void testGenerateId_hashVariation() {
        when(configService.getEnv()).thenReturn("PROD");

        String id1 = deviceIdGenerator.generateId("DeviceA", "BrandA");
        String id2 = deviceIdGenerator.generateId("DeviceB", "BrandB");

        assertThat(id1).isNotEqualTo(id2);
    }
}
