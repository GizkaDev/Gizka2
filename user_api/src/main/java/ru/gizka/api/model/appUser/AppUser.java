package ru.gizka.api.model.appUser;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.notification.Notification;
import ru.gizka.api.model.hero.Hero;

import java.util.*;

@Entity
@Table(name = "app_user",
        indexes = {@Index(name = "idx_login", columnList = "login")})
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private Date registeredAt;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "role",
            joinColumns = @JoinColumn(name = "app_user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"app_user_id", "role"}, name = "uc_user_role"))
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @NotNull
    private Set<Role> roles;

    @OneToMany(mappedBy = "appUser",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private List<Hero> heroes;

    @OneToMany(mappedBy = "appUser",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private List<Notification> notifications;

    public AppUser(String login, String password, Date registeredAt, Set<Role> roles, List<Hero> heroes, List<Notification> notifications) {
        this.login = login;
        this.password = password;
        this.registeredAt = registeredAt;
        this.roles = roles;
        this.heroes = heroes;
        this.notifications = notifications;
    }

    public AppUser(String login, String password) {
        this.login = login;
        this.password = password;
        this.registeredAt = new Date();
        this.roles = new HashSet<>();
        this.heroes = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }
}

