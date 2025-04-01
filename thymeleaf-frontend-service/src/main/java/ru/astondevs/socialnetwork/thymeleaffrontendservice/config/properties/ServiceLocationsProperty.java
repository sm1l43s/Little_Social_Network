package ru.astondevs.socialnetwork.thymeleaffrontendservice.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties
@Slf4j
public record ServiceLocationsProperty(Map<String, ServiceProperty> serviceLocations) {

    private record ServiceProperty(String name, String url, Map<String, EndpointProperty> endpoints) {
    }

    private record EndpointProperty(String baseUrl, Map<String, String> mappings) {
    }

    @PostConstruct
    public void init() {
        log.info("Service locations property configured");
    }
}
