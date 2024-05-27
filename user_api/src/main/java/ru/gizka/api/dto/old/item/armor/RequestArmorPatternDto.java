package ru.gizka.api.dto.old.item.armor;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.old.item.RequestItemPatternDto;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestArmorPatternDto extends RequestItemPatternDto {

    @PositiveOrZero(message = "Значение брони должно быть 0 или больше")
    @NotNull(message = "Значение брони должно быть 0 или больше")
    private Integer armor;

    @NegativeOrZero(message = "Значение штрафа к ловкости должно быть 0 или меньше")
    @NotNull(message = "Значение штрафа к ловкости должно быть 0 или меньше")
    private Integer dexPenalty;

    @Pattern(regexp = "CLOTHES|LIGHT|MEDIUM|HEAVY", message = "Недействительный тип доспехов")
    @NotNull(message = "Недействительный тип доспехов")
    private String armorType;

    public RequestArmorPatternDto(String name, Long weight, Integer value,
                                  Integer armor, Integer dexPenalty, String armorType) {
        super(name, weight, value, "Доспехи");
        this.armor = armor;
        this.dexPenalty = dexPenalty;
        this.armorType = armorType;
    }
}
