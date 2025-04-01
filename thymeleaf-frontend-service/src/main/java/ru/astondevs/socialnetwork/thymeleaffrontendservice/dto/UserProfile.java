package ru.astondevs.socialnetwork.thymeleaffrontendservice.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserProfile(
        UUID id,

        String firstName,
        String lastName,
        String email,
        String avatarUrl,

        Set<UUID> subscriptionUserIds,
        Set<UUID> followerUserIds,

        LocalDateTime registrationDate,
        LocalDateTime lastEntered) {
}
