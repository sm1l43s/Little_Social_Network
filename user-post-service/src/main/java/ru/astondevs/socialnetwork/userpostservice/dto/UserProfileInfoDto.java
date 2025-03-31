package ru.astondevs.socialnetwork.userpostservice.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserProfileInfoDto(
        UUID id,

        String firstName,
        String lastName,
        String email,
        String avatarUrl,

        Set<UUID> subscriptionUserIds,
        Set<UUID> followerUserIds,

        LocalDateTime registrationDate,
        LocalDateTime lastEntered) {

    public int getTotalSubscriptions() {
        return subscriptionUserIds.size();
    }

    public int getTotalFollowers() {
        return followerUserIds.size();
    }
}
