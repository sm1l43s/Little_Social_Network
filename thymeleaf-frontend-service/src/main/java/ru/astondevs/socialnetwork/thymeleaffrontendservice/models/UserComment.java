package ru.astondevs.socialnetwork.thymeleaffrontendservice.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record UserComment(
        Long id,

        UUID userId,
        String userFirstName,
        String userLastName,
        String userAvatarUrl,

        String message,

        LocalDate date,
        LocalTime time) {

}
