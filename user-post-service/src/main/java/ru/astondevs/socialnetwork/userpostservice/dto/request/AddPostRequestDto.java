package ru.astondevs.socialnetwork.userpostservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddPostRequestDto(
        @NotBlank(message = "No post content in request body. Parameter 'message' is missing")
        String message) {
}
