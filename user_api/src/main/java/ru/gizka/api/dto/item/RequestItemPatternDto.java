package ru.gizka.api.dto.item;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemPatternDto {

    @Size(min = 1, max = 200, message = "Название шаблона предмета должно состоять из 1-200 символов")
    @NotBlank(message = "Название шаблона предмета должно состоять из 1-200 символов")
    private String name;

    @PositiveOrZero(message = "Вес шаблона предмета должны быть равен или больше 0")
    @NotNull(message = "Вес шаблона предмета должны быть равен или больше 0")
    private Long weight;

    @Positive(message = "Ценность шаблона предмета должна быть больше 0")
    @NotNull(message = "Ценность шаблона предмета должна быть больше 0")
    private Integer value;

    @NotBlank(message = "Тип шаблона предмета не выбран")
    @Size(min = 1 , max = 100, message = "Название шаблона предмета должно состоять из 1-200 символов")
    private String product;
}
