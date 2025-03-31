package ru.astondevs.socialnetwork.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NewPasswordDto(@NotBlank(message = "Parameter 'password' must not be null or empty")
                             String password) {
}
