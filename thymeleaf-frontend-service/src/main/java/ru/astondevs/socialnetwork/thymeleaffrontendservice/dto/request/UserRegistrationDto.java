package ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.validation.annotations.PasswordsMatch;

@PasswordsMatch
public record UserRegistrationDto(
        @NotBlank(message = "Введите фамилию")
        @Size(max = 30, message = "Фамилия не должна быть больше 30 символов")
        String lastName,

        @NotBlank(message = "Введите имя")
        @Size(max = 30, message = "Имя не должно быть больше 30 символов")
        String firstName,

        @NotBlank(message = "Введите email")
        @Email(message = "Email введен неверно")
        String email,

        @NotBlank(message = "Введите пароль")
        String password,

        @NotBlank(message = "Подтвердите пароль")
        String passwordRepeat) {
}