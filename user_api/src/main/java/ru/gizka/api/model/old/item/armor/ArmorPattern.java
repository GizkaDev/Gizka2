package ru.gizka.api.model.old.item.armor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.old.item.ItemPattern;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "armor_pattern")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArmorPattern extends ItemPattern {

    public ArmorPattern(String name, Long weight, Integer value,
                        Integer armor, Integer dexPenalty, ArmorType armorType) {
        super(name, weight, value);
        this.armor = armor;
        this.dexPenalty = dexPenalty;
        this.armorType = armorType;
    }

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