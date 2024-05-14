package ru.gizka.api.dto.item.armor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.dto.item.ResponseProductDto;
import ru.gizka.api.model.item.armor.ArmorType;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseArmorDto extends ResponseItemDto {
    private Integer armor;
    private Integer dexPenalty;
    private ArmorType armorType;

    public ResponseArmorDto(Long id, String name, Long weight, Integer value, ResponseProductDto productDto,
                            Integer armor, Integer dexPenalty, ArmorType armorType) {
        super(id, name, weight, value, productDto);
        this.armor = armor;
        this.dexPenalty = dexPenalty;
        this.armorType = armorType;
    }
}
