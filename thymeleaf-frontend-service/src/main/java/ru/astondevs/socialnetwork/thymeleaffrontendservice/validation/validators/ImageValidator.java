package ru.astondevs.socialnetwork.thymeleaffrontendservice.validation.validators;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.imageio.ImageIO;
import java.util.List;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.AVATAR_ERROR_MESSAGE;
import static ru.astondevs.socialnetwork.thymeleaffrontendservice.utils.AttributeNameUtil.POST_ERROR_MESSAGE;

@UtilityClass
public class ImageValidator {

    private static final List<String> ALLOWED_FORMATS = List.of(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, "image/webp");

    private static final int MAX_AVATAR_SIZE = 5_242_880; // 5MB
    private static final int MAX_POST_IMAGE_SIZE = 15_728_640; // 15MB

    @SneakyThrows
    public static void validateAvatar(MultipartFile avatar, RedirectAttributes redirectAttributes) {
        validate(avatar, MAX_AVATAR_SIZE, redirectAttributes, AVATAR_ERROR_MESSAGE);
    }

    @SneakyThrows
    public static void validatePostImage(MultipartFile image, RedirectAttributes redirectAttributes) {
        validate(image, MAX_POST_IMAGE_SIZE, redirectAttributes, POST_ERROR_MESSAGE);
    }

    @SneakyThrows
    private static void validate(MultipartFile file, int maxFileSize, RedirectAttributes redirectAttributes,
                                 String errorAttribute) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute(errorAttribute, "Файл не выбран");
            return;
        }

        if (file.getSize() > maxFileSize) {
            redirectAttributes.addFlashAttribute(errorAttribute, "Размер файла не более " + MAX_AVATAR_SIZE + " мегабайт");
            return;
        }

        if (!ALLOWED_FORMATS.contains(file.getContentType())) {
            redirectAttributes.addFlashAttribute(errorAttribute, "Разрешен формат: " + ALLOWED_FORMATS);
            return;
        }

        if (ImageIO.read(file.getInputStream()) == null) {
            redirectAttributes.addFlashAttribute(errorAttribute, "Не удалось прочитать файл");
        }
    }
}
