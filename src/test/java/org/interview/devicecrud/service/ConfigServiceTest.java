package org.interview.devicecrud.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConfigService}.
 */
class ConfigServiceTest {


    private final ApplicationContextRunner ctx =
            new ApplicationContextRunner().withUserConfiguration(ConfigService.class);


    @Test
    void whenPropertyMissing_envIsNull() {
        ctx.run(c -> {
            ConfigService cfg = c.getBean(ConfigService.class);
            assertThat(cfg.getEnv()).isNull();
        });
    }


    @Test
    void setterGetter_workInIsolation() {
        ConfigService cfg = new ConfigService();
        cfg.setEnv("dev");
        assertThat(cfg.getEnv()).isEqualTo("dev");
    }
}
