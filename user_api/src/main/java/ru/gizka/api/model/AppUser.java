package ru.gizka.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "app_user",
        indexes = {@Index(name = "idx_login", columnList = "login")})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", unique = true)
    @Size(min = 4, max = 255)
    @NotBlank
    private String login;

    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 255)
    @NotBlank
    private String password;

    @Column(name = "registered_at")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @PastOrPresent
    private Date registeredAt = new Date();

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "role",
            joinColumns = @JoinColumn(name = "app_user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"app_user_id", "role"}, name = "uc_user_role"))
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @NotNull
    private Set<Role> roles;
}

