package ru.gizka.api.dto.old.item.weapon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.old.item.ResponseItemDto;
import ru.gizka.api.dto.old.item.ResponseProductDto;
import ru.gizka.api.model.old.item.weapon.DamageType;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWeaponDto extends ResponseItemDto {
    private Double damageMult;
    private Boolean isTwoHanded;
    private Set<DamageType> damageTypes;

    public ResponseWeaponDto(Long id, String name, Long weight, Integer value, ResponseProductDto productDto,
                             Double damageMult, Boolean isTwoHanded, Set<DamageType> damageTypes) {
        super(id, name, weight, value, productDto);
        this.damageMult = damageMult;
        this.isTwoHanded = isTwoHanded;
        this.damageTypes = damageTypes;
    }
}
