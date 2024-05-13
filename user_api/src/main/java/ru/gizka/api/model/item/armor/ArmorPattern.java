package ru.gizka.api.model.item.armor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.item.Product;

import java.util.List;


@Entity
@Table(name = "armor_pattern")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArmorPattern {

    public ArmorPattern(String name, Integer armor, ArmorType armorType) {
        this.name = name;
        this.armor = armor;
        this.armorType = armorType;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @Size(min = 1, max = 200)
    @NotBlank
    private String name;

    @Column(name = "armor")
    @PositiveOrZero
    private Integer armor;

    @Enumerated(EnumType.STRING)
    @Column(name = "armor_type")
    private ArmorType armorType;
}