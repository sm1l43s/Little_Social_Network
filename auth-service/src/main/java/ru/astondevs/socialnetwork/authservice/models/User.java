package ru.astondevs.socialnetwork.authservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "char(36)")
    @JdbcTypeCode(Types.CHAR)
    private UUID id;

    @Setter
    @Email
    private String email;

    @Setter
    @Pattern(regexp = "^\\$2[ayb]\\$[0-9]{2}\\$[./A-Za-z0-9]{53}$")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Setter
    @Column(name = "last_entered")
    @PastOrPresent
    private LocalDateTime lastEntered;

    @PrePersist
    protected void onCreate() {
        registrationDate = now().truncatedTo(SECONDS);
        lastEntered = now().truncatedTo(SECONDS);
    }

    @PreUpdate
    protected void onUpdate() {
        if (lastEntered.isBefore(registrationDate)) {
            throw new IllegalArgumentException("last entered is before registration date");
        }
    }
}
