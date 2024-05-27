package ru.gizka.api.dto.old.hero;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestHeroDto {

    @Size(min = 1, max = 50, message = "Имя героя должно состоять минимум из одного символа")
    @NotBlank(message = "Имя героя должно состоять минимум из одного символа")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Имя героя может состоять только из букв русского или латинского алфавита")
    private String name;

    @Size(min = 1, max = 100, message = "Фамилия героя должна состоять минимум из одного символа")
    @NotBlank(message = "Фамилия героя должна состоять минимум из одного символа")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]+$", message = "Фамилия героя может состоять только из букв русского или латинского алфавита")
    private String lastName;

    @Min(value = 5, message = "Сила должна быть не меньше 5")
    @NotNull(message = "Сила должна быть не меньше 5")
    private Integer str;

    @Min(value = 5, message = "Ловкость должна быть не меньше 5")
    @NotNull(message = "Ловкость должна быть не меньше 5")
    private Integer dex;

    @Min(value = 5, message = "Телосложение должно быть не меньше 5")
    @NotNull(message = "Телосложение должно быть не меньше 5")
    private Integer con;

    @Min(value = 5, message = "Мудрость должна быть не меньше 5")
    @NotNull(message = "Мудрость должна быть не меньше 5")
    private Integer wis;

    @NotBlank(message = "Раса не выбрана")
    @Size(min = 1, max = 100, message = "Название расы должно состоять минимум из одного символа")
    private String race;
}
