package ru.astondevs.socialnetwork.userpostservice.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.astondevs.socialnetwork.userpostservice.models.Post;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "comments")
    Optional<Post> findWithCommentsById(Long id);

    @EntityGraph(attributePaths = {"userIdsWhoLiked", "comments"})
    List<Post> findPostsByUserId(UUID userId);

    @EntityGraph(attributePaths = {"userIdsWhoLiked", "comments"})
    @Query("SELECT p FROM Post p WHERE p.userId IN :userIds")
    List<Post> findPostsByIds(Set<UUID> userIds);
}
