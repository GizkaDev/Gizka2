package ru.gizka.api.model.item.weapon;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.item.ItemPattern;
import ru.gizka.api.model.item.Product;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "weapon_pattern")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeaponPattern extends ItemPattern {

    public WeaponPattern(String name, Long weight, Integer value, Product product, List<Fight> fights,
                         Double damageMult, Boolean isTwoHanded, Set<DamageType> damageTypes) {
        super(name, weight, value, product, fights);
        this.damageMult = damageMult;
        this.isTwoHanded = isTwoHanded;
        this.damageTypes = damageTypes;
    }

    @Column(name = "damage_mult")
    @PositiveOrZero
    private Double damageMult;

    @Column(name = "is_two_handed")
    private Boolean isTwoHanded;

    @ElementCollection
    @CollectionTable(name = "weapon_damage_types",
            joinColumns = @JoinColumn(name = "weapon_pattern_id"))
    @Column(name = "damage_type")
    @Enumerated(EnumType.STRING)
    private Set<DamageType> damageTypes;
}
