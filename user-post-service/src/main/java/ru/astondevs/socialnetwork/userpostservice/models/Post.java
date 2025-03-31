package ru.astondevs.socialnetwork.userpostservice.models;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static java.time.temporal.ChronoUnit.SECONDS;

@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "char(36)")
    @JdbcTypeCode(Types.CHAR)
    @NotNull
    private UUID userId;

    @Column(columnDefinition = "longtext")
    @NotBlank
    private String message;

    private String imageUrl;

    @PastOrPresent
    private LocalDate date;

    private LocalTime time;

    @ElementCollection
    @CollectionTable(name = "posts_likes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "user_id", columnDefinition = "char(36)")
    @Builder.Default
    private Set<UUID> userIdsWhoLiked = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = LAZY, cascade = { MERGE, REMOVE }, orphanRemoval = true)
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
        time = LocalTime.now();
    }

    public int getTotalComments() {
        return comments.size();
    }
}
