package ru.gizka.api.model.old.fight;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.old.hero.Hero;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "duel")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Duel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "duels")
    private List<Hero> heroes;

    @Column(name = "turns")
    @Lob
    private String turns;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private Result result;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Duel(List<Hero> heroes, String turns, Result result, Date createdAt) {
        this.heroes = heroes;
        this.turns = turns;
        this.result = result;
        this.createdAt = createdAt;
    }
}
