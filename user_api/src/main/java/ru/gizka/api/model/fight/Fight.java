package ru.gizka.api.model.fight;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.hero.Hero;

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

    @ManyToMany(mappedBy = "fights")
    private List<Hero> heroes;

    @Column(name = "turns")
    @Lob
    private String turns;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
