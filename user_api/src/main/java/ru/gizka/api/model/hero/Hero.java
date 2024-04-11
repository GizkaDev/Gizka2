package ru.gizka.api.model.hero;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.user.AppUser;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "hero")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hero {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Size(min = 1, max = 50)
    @NotBlank
    private String name;

    @Column(name = "lastname")
    @Size(min = 1, max = 100)
    @NotBlank
    private String lastname;

    @Column(name = "str")
    @Positive
    private Integer str;

    @Column(name = "dex")
    @Positive
    private Integer dex;

    @Column(name = "con")
    @Positive
    private Integer con;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "app_user_login", referencedColumnName = "login")
    private AppUser appUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hero_duel",
            joinColumns = @JoinColumn(name = "hero_id"),
            inverseJoinColumns = @JoinColumn(name = "duel_id")
    )
    private List<Duel> duels;

    @ManyToOne
    @JoinColumn(name = "hero_race", referencedColumnName = "name")
    private Race race;
}
