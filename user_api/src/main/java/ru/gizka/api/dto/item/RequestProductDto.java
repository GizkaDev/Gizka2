package ru.gizka.api.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestProductDto {
    @Size(min = 3, max = 100, message = "Название категории товара должно состоять из 3-255 знаков")
    @NotBlank(message = "Название категории товара должно состоять из 3-255 знаков")
    private String name;

    @PositiveOrZero(message = "Цена товара должна быть больше или равна 0")
    @NotNull(message = "Цена товара должна быть больше или равна 0")
    private Integer price;
}
