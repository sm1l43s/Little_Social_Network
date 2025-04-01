package ru.astondevs.socialnetwork.thymeleaffrontendservice.models;

import java.util.UUID;

public record SuggestionUserInfo(
        UUID id,

        String firstName,
        String lastName,
        String avatarUrl,

        int followersCount) {
}
