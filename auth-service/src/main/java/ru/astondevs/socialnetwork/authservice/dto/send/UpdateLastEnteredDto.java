package ru.astondevs.socialnetwork.authservice.dto.send;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateLastEnteredDto(UUID userId, LocalDateTime lastEntered) {
}
