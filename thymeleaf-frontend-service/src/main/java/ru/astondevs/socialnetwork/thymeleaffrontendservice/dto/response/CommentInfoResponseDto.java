package ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CommentInfoResponseDto(

        long commentId,
        String message,

        UUID userId,
        String userLastName,
        String userFirstName,

        LocalDate date,
        LocalTime time,

        int totalComments
) {
}
