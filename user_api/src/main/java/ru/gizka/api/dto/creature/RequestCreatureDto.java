package ru.gizka.api.dto.creature;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @NotBlank(message = "Раса не выбрана")
    @Size(min = 1, max = 100, message = "Название расы должно состоять минимум из одного символа")
    private String race;
}
