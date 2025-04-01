package ru.astondevs.socialnetwork.thymeleaffrontendservice.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserPost(
        Long id,

        UUID userId,
        String userFirstName,
        String userLastName,
        String userAvatarUrl,

        String message,
        String imageUrl,

        LocalDate date,
        LocalTime time,

        Set<UUID> userIdsWhoLiked,

        List<UserComment> comments) {

    public int getLikesCount() {
        return userIdsWhoLiked.size();
    }

    public int getCommentsCount() {
        return comments.size();
    }
}
