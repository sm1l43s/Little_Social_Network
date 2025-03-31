package ru.astondevs.socialnetwork.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.astondevs.socialnetwork.authservice.models.User;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.lastEntered = :lastEntered WHERE u.id = :id")
    void updateLastEntered(UUID id, LocalDateTime lastEntered);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :id")
    void changePassword(UUID id, String newPassword);
}
