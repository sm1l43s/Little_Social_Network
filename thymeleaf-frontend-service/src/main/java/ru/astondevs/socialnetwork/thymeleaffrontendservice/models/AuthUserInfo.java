package ru.astondevs.socialnetwork.thymeleaffrontendservice.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record AuthUserInfo(
        UUID id,

        String firstName,
        String lastName,
        String email,
        String avatarUrl,

        Set<UUID> subscriptionUserIds,
        Set<UUID> followerUserIds,

        List<UserPost> posts,

        List<SuggestionUserInfo> suggestionUsersInfo,

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
}
