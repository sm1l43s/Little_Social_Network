package ru.astondevs.socialnetwork.userprofileservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateLastEnteredDto(@NotNull(message = "Missing 'userId' parameter")
                                   UUID userId,

                                   @NotNull(message = "Missing 'lastEntered' parameter")
                                   @PastOrPresent(message = "Incorrect 'lastEntered' parameter")
                                   LocalDateTime lastEntered) {
}
