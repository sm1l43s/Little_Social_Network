package ru.astondevs.socialnetwork.userprofileservice.dto.response;

import java.util.UUID;

public record SuggestionUserInfoDto(
        UUID id,

        String firstName,
        String lastName,
        String avatarUrl,

        int followersCount) {
}
