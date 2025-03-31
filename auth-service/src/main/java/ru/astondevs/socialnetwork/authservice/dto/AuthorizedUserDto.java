package ru.astondevs.socialnetwork.authservice.dto;

import ru.astondevs.socialnetwork.authservice.models.Role;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthorizedUserDto(
        UUID id,

        String firstName,
        String lastName,
        String email,
        String avatarUrl,

        Role role,

        LocalDateTime registrationDate,
        LocalDateTime lastEntered)  {
}
