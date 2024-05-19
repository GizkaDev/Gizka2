package ru.gizka.api.dto.item.weapon;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.model.item.weapon.DamageType;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestWeaponPatternDto extends RequestItemPatternDto {

    @PositiveOrZero(message = "Мультипликатор урона должен быть 0.0 или больше")
    @NotNull(message = "Мультипликатор урона должен быть 0.0 или больше")
    private Double damageMult;

    @NotNull(message = "Необходимо указать, является ли оружие двуручным")
    private Boolean isTwoHanded;

    @Enumerated(EnumType.STRING)
    private Set<DamageType> damageTypes;

    public RequestWeaponPatternDto(String name, Long weight, Integer value,
                                   Double damageMult, Boolean isTwoHanded, Set<DamageType> damageTypes) {
        super(name, weight, value, "Оружие");
        this.damageMult = damageMult;
        this.isTwoHanded = isTwoHanded;
        this.damageTypes = damageTypes;
    }
}
