package ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginDto(
        @NotBlank(message = "Введите email")
        @Email(message = "Email введен неверно")
        String email,

        @NotBlank(message = "Введите пароль")
        String password) {
}
