package ru.astondevs.socialnetwork.userpostservice.validation.validators;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@UtilityClass
public class ImageValidator {

    private static final List<String> ALLOWED_FORMATS = List.of(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, "image/webp");

    @SneakyThrows
    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "File content is missing");
        }

        if (!ALLOWED_FORMATS.contains(file.getContentType())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid format. Allowed format: " + ALLOWED_FORMATS);
        }

        if (ImageIO.read(file.getInputStream()) == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Failed to read file contents");
        }
    }
}
