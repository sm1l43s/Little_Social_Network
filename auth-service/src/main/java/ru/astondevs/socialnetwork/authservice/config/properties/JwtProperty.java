package ru.astondevs.socialnetwork.authservice.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
@Slf4j
public record JwtProperty(String key, long expirationTime) {

    @PostConstruct
    public void init() {
        log.info("Jwt properties applied: key={}, expirationTime={}", key, expirationTime);
    }
}
