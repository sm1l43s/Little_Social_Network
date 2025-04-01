package ru.astondevs.socialnetwork.userpostservice.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CommentResponseDto(
        Long id,

        UUID userId,
        String userFirstName,
        String userLastName,
        String userAvatarUrl,

        String message,

        LocalDate date,
        LocalTime time) {
}
