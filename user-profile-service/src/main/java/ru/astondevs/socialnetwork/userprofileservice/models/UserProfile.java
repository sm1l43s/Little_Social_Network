package ru.astondevs.socialnetwork.userprofileservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProfile {

    @Id
    @Column(columnDefinition = "char(36)")
    @JdbcTypeCode(Types.CHAR)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Email
    @EqualsAndHashCode.Include
    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribed_user_id")
    )
    @Builder.Default
    private Set<UserProfile> subscriptions = new HashSet<>();

    @ManyToMany(mappedBy = "subscriptions")
    @Builder.Default
    private Set<UserProfile> followers = new HashSet<>();

    @Column(name = "registration_date")
    @PastOrPresent
    private LocalDateTime registrationDate;

    @Column(name = "last_entered")
    @PastOrPresent
    private LocalDateTime lastEntered;

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        if (lastEntered.isBefore(registrationDate)) {
            throw new IllegalArgumentException("last entered is before registration date");
        }
    }

    public int getTotalSubscriptions() {
        return subscriptions.size();
    }

    public int getTotalFollowers() {
        return followers.size();
    }
}
