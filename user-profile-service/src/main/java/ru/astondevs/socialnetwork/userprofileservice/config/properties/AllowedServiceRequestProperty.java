package ru.astondevs.socialnetwork.userprofileservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties(prefix = "allowed-requests-from-services")
public record AllowedServiceRequestProperty(Map<String, ServiceConfig> serviceLocations) {

    public record ServiceConfig(String name, String ip) {

    }
}
