package ru.gizka.api.model.item.armor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "armor_pattern")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArmorPattern {

    public ArmorPattern(String name, Integer armor, Integer dexPenalty, ArmorType armorType) {
        this.name = name;
        this.armor = armor;
        this.dexPenalty = dexPenalty;
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

    @Column(name = "dex_penalty")
    @NegativeOrZero
    private Integer dexPenalty;

    @Enumerated(EnumType.STRING)
    @Column(name = "armor_type")
    private ArmorType armorType;
}