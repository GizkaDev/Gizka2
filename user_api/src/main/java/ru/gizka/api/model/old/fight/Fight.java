package ru.gizka.api.model.old.fight;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.old.creature.Creature;
import ru.gizka.api.model.old.hero.Hero;
import ru.gizka.api.model.old.item.ItemPattern;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "fight")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fight {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hero_fight", referencedColumnName = "id")
    private Hero hero;

    @ManyToOne
    @JoinColumn(name = "creature_fight", referencedColumnName = "name")
    private Creature creature;

    @Column(name = "turns")
    @Lob
    private String turns;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private Result result;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToMany(mappedBy = "fights")
    private List<ItemPattern> loot;

    public Fight(Hero hero, Creature creature, String turns, Result result, Date createdAt) {
        this.hero = hero;
        this.creature = creature;
        this.turns = turns;
        this.result = result;
        this.createdAt = createdAt;
    }
}
