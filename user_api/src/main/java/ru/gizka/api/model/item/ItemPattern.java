package ru.gizka.api.model.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.fight.Fight;

import java.util.List;

@Entity
@Table(name = "item_pattern")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class ItemPattern {

    public ItemPattern(String name, Long weight, Integer value) {
        this.name = name;
        this.weight = weight;
        this.value = value;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", unique = true)
    @Size(min = 1, max = 200)
    @NotBlank
    private String name;

    @Column(name = "weight")
    @PositiveOrZero
    private Long weight;

    @Column(name = "item_value")
    @Positive
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "item_pattern_product", referencedColumnName = "name")
    private Product product;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_pattern_fight",
            joinColumns = @JoinColumn(name = "item_pattern_id"),
            inverseJoinColumns = @JoinColumn(name = "fight_id"))
    private List<Fight> fights;
}
