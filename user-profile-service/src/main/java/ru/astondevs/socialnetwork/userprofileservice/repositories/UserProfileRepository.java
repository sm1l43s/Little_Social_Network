package ru.astondevs.socialnetwork.userprofileservice.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.astondevs.socialnetwork.userprofileservice.models.UserProfile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO user_profiles (id, first_name, last_name, email, avatar_url, registration_date, last_entered)
            VALUES (:#{#userProfile.id.toString()}, :#{#userProfile.firstName}, :#{#userProfile.lastName},
                    :#{#userProfile.email}, :#{#userProfile.avatarUrl}, :#{#userProfile.registrationDate},
                    :#{#userProfile.lastEntered})""",
            nativeQuery = true)
    void create(UserProfile userProfile);

    @Modifying
    @Query("UPDATE UserProfile up SET up.avatarUrl = :avatarUrl WHERE up.id = :userId")
    void setAvatar(UUID userId, String avatarUrl);

    @EntityGraph(attributePaths = {"subscriptions", "followers"})
    @Query("SELECT u FROM UserProfile u WHERE u.id = :id")
    Optional<UserProfile> findUserProfileInfo(UUID id);

    @Modifying
    @Query("UPDATE UserProfile up SET up.lastEntered = :lastEntered WHERE up.id = :id")
    void updateLastEntered(UUID id, LocalDateTime lastEntered);

    @Modifying
    @Query(value = """
            INSERT INTO user_subscriptions (user_id, subscribed_user_id)
            VALUES (:#{#userId.toString()}, :#{#subscribeUserId.toString()})""",
            nativeQuery = true)
    void subscribe(UUID userId, UUID subscribeUserId);

    @Modifying
    @Query(value = """
            DELETE FROM user_subscriptions
            WHERE user_id = :#{#userId.toString()} AND subscribed_user_id = :#{#unsubscribeUserId.toString()}""",
            nativeQuery = true)
    void unsubscribe(UUID userId, UUID unsubscribeUserId);

    @Query(value = "SELECT id FROM user_profiles WHERE id != :#{#userId.toString()} ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<UUID> findRandomUserIdsExceptUserId(UUID userId);

    @EntityGraph(attributePaths = "followers")
    @Query("SELECT up FROM UserProfile up WHERE up.id IN :userIds")
    List<UserProfile> findUserProfilesByIds(List<UUID> userIds);
}
