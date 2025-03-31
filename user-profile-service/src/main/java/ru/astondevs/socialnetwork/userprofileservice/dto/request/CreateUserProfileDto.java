package ru.astondevs.socialnetwork.userprofileservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateUserProfileDto(@NotNull(message = "Missing 'id' parameter")
                                   UUID id,

                                   @NotBlank(message = "Parameter 'firstName' must not be null or empty")
                                   @Size(max = 30, message = "Parameter 'firstName' length must be no more than 30 chars")
                                   String firstName,

                                   @NotBlank(message = "Parameter 'lastName' must not be null or empty")
                                   @Size(max = 30, message = "Parameter 'lastName' length must be no more than 30 chars")
                                   String lastName,

                                   @NotBlank(message = "Parameter 'email' must not be null or empty")
                                   @Email(message = "Incorrect 'email' parameter")
                                   String email,

                                   @NotNull(message = "Missing 'registrationDate' parameter")
                                   @PastOrPresent(message = "Incorrect 'registrationDate' parameter")
                                   LocalDateTime registrationDate,

                                   @NotNull(message = "Missing 'lastEntered' parameter")
                                   @PastOrPresent(message = "Incorrect 'lastEntered' parameter")
                                   LocalDateTime lastEntered) {
}