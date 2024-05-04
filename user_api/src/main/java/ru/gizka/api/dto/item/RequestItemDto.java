package ru.gizka.api.dto.item;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.item.Product;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestItemDto {

    @Size(min = 1, max = 200, message = "Название предмета должно состоять из 1-200 символов")
    @NotBlank(message = "Название предмета должно состоять из 1-200 символов")
    private String name;

    @PositiveOrZero(message = "Вес предмета должны быть равен или больше 0")
    private Long weight;

    @Positive(message = "Ценность предмета должна быть больше 0")
    private Integer value;

    @NotBlank(message = "Тип предмета не выбран")
    @Size(min = 1 , max = 100, message = "Название предмета должно состоять из 1-200 символов")
    private String product;
}
