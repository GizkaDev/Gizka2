package ru.gizka.api.dto.item.armor;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestArmorPatternDto {

    @Size(min = 1, max = 200, message = "Название шаблона доспехов должно состоять из 1-200 символов")
    @NotBlank(message = "Название шаблона доспехов должно состоять из 1-200 символов")
    private String name;

    @PositiveOrZero(message = "Значение брони должно быть 0 или больше")
    private Integer armor;

    @NegativeOrZero(message = "Значение штрафа к ловкости должно быть 0 или меньше")
    private Integer dexPenalty;

    @Pattern(regexp = "CLOTHES|LIGHT|MEDIUM|HEAVY", message = "Недействительный тип доспехов")
    private String armorType;
}
