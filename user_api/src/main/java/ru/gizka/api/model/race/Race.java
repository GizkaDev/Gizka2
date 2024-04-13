package ru.gizka.api.model.race;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "race")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Race {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @Size(min = 4, max = 255)
    @NotBlank
    private String name;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "is_playable")
    @NotNull
    private Boolean isPlayable;

    @OneToMany(mappedBy = "race",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private List<Hero> heroes;

    @OneToMany(mappedBy = "race",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private List<Creature> creatures;
}
