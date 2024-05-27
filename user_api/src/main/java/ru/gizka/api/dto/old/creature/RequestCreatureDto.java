package ru.gizka.api.dto.old.creature;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreatureDto {
    @Size(min = 1, max = 100, message = "Название моба должно состоять из 1 - 100 символов")
    @NotBlank(message = "Название моба должно состоять из 1 - 100 символов")
    private String name;

    @NotNull(message = "Сила не должна быть пустой")
    @Positive(message = "Сила должна быть больше 0")
    private Integer str;

    @NotNull(message = "Ловкость не должна быть пустой")
    @Positive(message = "Ловкость должна быть больше 0")
    private Integer dex;

    @NotNull(message = "Выносливость не должна быть пустой")
    @Positive(message = "Выносливость должна быть больше 0")
    private Integer con;

    @NotNull(message = "Защита должна быть 0 или больше")
    @PositiveOrZero(message = "Защита должна быть 0 или больше")
    private Integer def;

    @NotBlank(message = "Раса не выбрана")
    @Size(min = 1, max = 100, message = "Название расы должно состоять минимум из одного символа")
    private String race;
}
