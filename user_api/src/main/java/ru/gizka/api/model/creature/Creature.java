package ru.gizka.api.model.creature;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.race.Race;

import java.util.Date;

@Entity
@Table(name = "creature")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Creature {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
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
}
