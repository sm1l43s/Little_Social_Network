package ru.astondevs.socialnetwork.userpostservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.socialnetwork.userpostservice.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
