package ru.gizka.api.model.old.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.old.hero.Hero;

@Entity
@Table(name = "item_object")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class ItemObject {

    public ItemObject(String name, Long weight, Integer value) {
        this.name = name;
        this.weight = weight;
        this.value = value;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
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
    @JoinColumn(name = "item_object_product", referencedColumnName = "name")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "item_object_hero", referencedColumnName = "id")
    private Hero hero;
}
