package ru.gizka.api.model.old.creature;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.old.fight.Fight;
import ru.gizka.api.model.old.race.Race;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "creature")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Creature {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @Size(min = 1, max = 100)
    @NotBlank
    private String name;

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
    @JoinColumn(name = "creature_race", referencedColumnName = "name")
    private Race race;

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

    @Column(name = "current_hp")
    private Integer currentHp;

    @Column(name = "endurance")
    private Integer endurance;

    @Column(name = "def")
    private Integer def;

    @OneToMany(mappedBy = "creature",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private List<Fight> fights;

    public Creature(String name, Integer str, Integer dex, Integer con, Integer def) {
        this.name = name;
        this.str = str;
        this.dex = dex;
        this.con = con;
        this.def = def;
    }
}
