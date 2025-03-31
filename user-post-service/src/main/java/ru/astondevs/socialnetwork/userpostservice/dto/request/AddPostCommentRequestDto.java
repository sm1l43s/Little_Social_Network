package ru.astondevs.socialnetwork.userpostservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddPostCommentRequestDto(

        @NotNull(message = "Missing 'postId' parameter")
        @Positive(message = "Incorrect 'postId' parameter")
        Long postId,

        @NotBlank(message = "Missing 'comment' parameter")
        String comment
) {
}
