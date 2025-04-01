package ru.astondevs.socialnetwork.thymeleaffrontendservice.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserInfo(
        UUID id,

        String firstName,
        String lastName,
        String email,
        String avatarUrl,

        Set<UUID> subscriptionUserIds,
        Set<UUID> followerUserIds,

        List<UserPost> posts,

        LocalDateTime registrationDate,
        LocalDateTime lastEntered) {

    public int getTotalLikes() {
        int totalLikes = 0;

        for (var post : posts) {
            totalLikes += post.getLikesCount();
        }

        return totalLikes;
    }

    public int getTotalSubscriptions() {
        return subscriptionUserIds.size();
    }

    public int getTotalFollowers() {
        return followerUserIds.size();
    }

    public int getTotalPosts() {
        return posts.size();
    }

    public String getFormattedLastEntered(String pattern) {
        return lastEntered.format(DateTimeFormatter.ofPattern(pattern));
    }
}
