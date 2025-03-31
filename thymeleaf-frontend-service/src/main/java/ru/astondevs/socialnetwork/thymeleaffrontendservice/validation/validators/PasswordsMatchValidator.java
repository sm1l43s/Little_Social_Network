package ru.astondevs.socialnetwork.thymeleaffrontendservice.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request.UserRegistrationDto;
import ru.astondevs.socialnetwork.thymeleaffrontendservice.validation.annotations.PasswordsMatch;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, UserRegistrationDto> {

    @Override
    public boolean isValid(UserRegistrationDto userRegistrationDto, ConstraintValidatorContext context) {
        if (userRegistrationDto == null) {
            return true;
        }

        return userRegistrationDto.password().equals(userRegistrationDto.passwordRepeat());
    }
}
