package ru.gizka.api.dto.race;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRaceDto {

    @Size(min = 1, max = 100, message = "Название расы должно состоять минимум из одного символа")
    @NotBlank(message = "Название расы должно состоять минимум из одного символа")
    @Pattern(regexp = "^[а-яА-Я-]+$", message = "Название расы может состоять только из букв русского алфавита и тире ")
    private String name;

    @NotNull(message = "Не указана играбельность расы")
    private Boolean isPlayable;

    @Range(min = -5, max = 5, message = "Бонус силы должен быть в диапазоне от -5 до 5")
    @NotNull(message = "Бонус силы должен быть в диапазоне от -5 до 5")
    private Integer strBonus;

    @Range(min = -5, max = 5, message = "Бонус ловкости должен быть в диапазоне от -5 до 5")
    @NotNull(message = "Бонус ловкости должен быть в диапазоне от -5 до 5")
    private Integer dexBonus;

    @Range(min = -5, max = 5, message = "Бонус телосложения должен быть в диапазоне от -5 до 5")
    @NotNull(message = "Бонус телосложения должен быть в диапазоне от -5 до 5")
    private Integer conBonus;

    @Range(min = -5, max = 5, message = "Бонус мудрости должен быть в диапазоне от -5 до 5")
    @NotNull(message = "Бонус мудрости должен быть в диапазоне от -5 до 5")
    private Integer wisBonus;
}
