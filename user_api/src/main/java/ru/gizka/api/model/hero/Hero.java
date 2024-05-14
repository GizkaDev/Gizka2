package ru.gizka.api.model.hero;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.item.ItemObject;
import ru.gizka.api.model.item.armor.ArmorObject;
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
    @Column(name = "wis")
    @Positive
    private Integer wis;

    @Column(name = "min_init")
    private Integer minInit;
    @Column(name = "max_init")
    private Integer maxInit;
    @Column(name = "min_attack")
    private Integer minAttack;
    @Column(name = "max_attack")
    private Integer maxAttack;
    @Column(name = "min_evasion")
    private Integer minEvasion;
    @Column(name = "max_evasion")
    private Integer maxEvasion;
    @Column(name = "min_phys_damage")
    private Integer minPhysDamage;
    @Column(name = "max_phys_damage")
    private Integer maxPhysDamage;
    @Column(name = "max_hp")
    private Integer maxHp;
    @Column(name = "endurance")
    private Integer endurance;
    @Column(name = "maxWeight")
    private Long maxWeight;
    @Column(name = "search")
    private Integer search;
    @Column(name = "treat")
    private Integer treat;

    @Column(name = "current_hp")
    private Integer currentHp;
    @Column(name = "current_weight")
    private Long currentWeight;

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
            inverseJoinColumns = @JoinColumn(name = "duel_id"))
    private List<Duel> duels;

    @ManyToOne
    @JoinColumn(name = "hero_race", referencedColumnName = "name")
    private Race race;

    @OneToMany(mappedBy = "hero",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private List<Fight> fights;

    @Column(name = "treat_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date treatAt;

    @OneToMany(mappedBy = "hero",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private List<ItemObject> inventory;
}
