package ru.astondevs.socialnetwork.userpostservice.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties(prefix = "controller")
@Slf4j
public record ControllerProperty(Map<String, EndpointProperty> endpoints) {

    private record EndpointProperty(String basePath, Map<String, String> mappings) { }

    @PostConstruct
    public void init() {
        log.info("Controller request mapping configured");
    }
}
