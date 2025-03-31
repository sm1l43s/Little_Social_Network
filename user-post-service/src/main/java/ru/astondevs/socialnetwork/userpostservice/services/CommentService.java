package ru.astondevs.socialnetwork.userpostservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.socialnetwork.userpostservice.models.Comment;
import ru.astondevs.socialnetwork.userpostservice.models.Post;
import ru.astondevs.socialnetwork.userpostservice.repositories.CommentRepository;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public Comment save(UUID userId, String message, Post post) {
        var comment = Comment.builder()
                .userId(userId)
                .message(message)
                .post(post)
                .build();

        return commentRepository.save(comment);
    }
}
