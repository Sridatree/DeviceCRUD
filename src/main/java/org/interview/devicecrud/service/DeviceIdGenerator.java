package org.interview.devicecrud.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeviceIdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(DeviceIdGenerator.class);
    private final ConfigService config;

    public DeviceIdGenerator(ConfigService config) {
        this.config = config;
    }

    /**
     * This method generates the device id which is a combination
     * of the name and brand and hash of the name and brand.This is to
     * ensure that every device has an unique id serving as the primary key.
     *
     * @param name name of the device
     * @param brand brand of the device
     * @return unique device id
     */

    public String generateId(String name, String brand) {
        String encodedName = shorten(name, 4);
        String encodedBrand = shorten(brand, 4);

        String toHash = name + ":" + brand;
        String hash = DigestUtils.sha256Hex(toHash).substring(0, 6).toUpperCase();

        String deviceId =  String.format("%s-%s-%s-%s", config.getEnv(), encodedName, encodedBrand, hash);
        logger.debug("Generated device id {}", deviceId);
        logger.info("Device id generated!");
        return deviceId;
    }

    /**
     * Shortens the given input string to a specified maximum length after removing
     * all non-alphanumeric characters and converting it to uppercase.
     *
     * @param input     the input string to be shortened
     * @param maxLength the maximum length of the returned string
     * @return a shortened, uppercase, alphanumeric-only version of the input string,
     *         or "UNK" if the input is {@code null}
     */
    private String shorten(String input, int maxLength) {
        if (input == null) return "UNK";
        String alphanumeric = input.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        return alphanumeric.substring(0, Math.min(maxLength, alphanumeric.length()));
    }
}
