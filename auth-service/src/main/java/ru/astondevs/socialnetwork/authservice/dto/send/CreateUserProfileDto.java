package ru.astondevs.socialnetwork.authservice.dto.send;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateUserProfileDto(UUID id,
                                   String firstName,
                                   String lastName,
                                   String email,
                                   LocalDateTime registrationDate,
                                   LocalDateTime lastEntered) {
}