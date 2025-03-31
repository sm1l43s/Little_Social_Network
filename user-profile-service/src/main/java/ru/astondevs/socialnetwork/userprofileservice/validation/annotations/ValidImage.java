package ru.astondevs.socialnetwork.userprofileservice.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.astondevs.socialnetwork.userprofileservice.validation.validators.ImageValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

    String message() default "Avatar must be an image in the format (jpg, png, jpeg, webp) no more than 5 MB";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
