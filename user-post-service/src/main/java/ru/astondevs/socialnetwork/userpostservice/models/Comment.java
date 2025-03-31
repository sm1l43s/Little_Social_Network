package ru.astondevs.socialnetwork.userpostservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(exclude = "post")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "char(36)")
    @JdbcTypeCode(Types.CHAR)
    @NotNull
    private UUID userId;

    @ManyToOne(fetch = LAZY)
    private Post post;

    @Column(columnDefinition = "longtext")
    @NotBlank
    private String message;

    @PastOrPresent
    private LocalDate date;

    private LocalTime time;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
        time = LocalTime.now();
    }
}
