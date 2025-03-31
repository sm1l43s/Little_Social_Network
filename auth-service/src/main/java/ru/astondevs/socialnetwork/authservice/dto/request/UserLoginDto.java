package ru.astondevs.socialnetwork.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginDto(@NotBlank(message = "Parameter 'email' must not be null or empty")
                           @Email(message = "Incorrect 'email' parameter")
                           String email,

                           @NotBlank(message = "Parameter 'password' must not be null or empty")
                           String password) {
}
