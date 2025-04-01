package ru.astondevs.socialnetwork.userpostservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record PostResponseDto(
        Long id,

        UUID userId,
        String userFirstName,
        String userLastName,
        String userAvatarUrl,

        String message,
        String imageUrl,

        LocalDate date,
        LocalTime time,

        Set<UUID> userIdsWhoLiked,

        @JsonProperty("comments")
        List<CommentResponseDto> commentResponseDtos) {

        public int getLikesCount() {
                return userIdsWhoLiked.size();
        }

        public int getCommentsCount() {
                return commentResponseDtos.size();
        }
}
