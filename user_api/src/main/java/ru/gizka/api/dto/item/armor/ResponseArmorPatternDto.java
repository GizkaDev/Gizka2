package ru.gizka.api.dto.item.armor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.item.armor.ArmorType;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseArmorPatternDto {
    private String name;
    private Integer armor;
    private Integer dexPenalty;
    private ArmorType armorType;
}
