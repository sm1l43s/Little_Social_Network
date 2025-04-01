package ru.astondevs.socialnetwork.userprofileservice.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import ru.astondevs.socialnetwork.userprofileservice.validation.annotations.ValidImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private static final List<String> ALLOWED_FORMATS = List.of(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, "image/webp");

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (file == null || file.isEmpty()) {
            customMessageForValidation(context, "File is missing");
            return false;
        }

        if (!ALLOWED_FORMATS.contains(file.getContentType())) {
            customMessageForValidation(context, "Invalid format. Allowed format: " + ALLOWED_FORMATS);
            return false;
        }

        try {
            if (ImageIO.read(file.getInputStream()) == null) {
                customMessageForValidation(context, "Failed to read file contents");
                return false;
            }
        } catch (IOException exception) {
            return false;
        }

        return true;
    }

    private void customMessageForValidation(ConstraintValidatorContext constraintContext, String message) {
        constraintContext.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
