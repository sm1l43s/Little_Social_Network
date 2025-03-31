package ru.astondevs.socialnetwork.userpostservice.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Arrays;
import java.util.Map;

@ConfigurationProperties(prefix = "security")
@Slf4j
public record OpenPathProperty(Map<String, String[]> openPaths) {

    @PostConstruct
    public void init() {
        if (openPaths == null) {
            log.warn("No open paths configured");
            return;
        }

        log.info("Open paths configured");

        for (var entry : openPaths.entrySet()) {
            var method = entry.getKey();
            var paths = entry.getValue();

            log.info("Method: {} Paths: {}", method, Arrays.toString(paths));
        }
    }
}