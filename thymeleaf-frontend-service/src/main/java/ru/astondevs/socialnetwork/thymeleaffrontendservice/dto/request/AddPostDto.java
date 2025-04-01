package ru.astondevs.socialnetwork.thymeleaffrontendservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record AddPostDto(

        @NotBlank(message = "Содержимое поста не может быть пустым")
        String message,

        @JsonIgnore
        MultipartFile file) {
}
