package ru.gizka.api.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseItemDto {
    private Long id;
    private String name;
    private Long weight;
    private Integer value;
    private ResponseProductDto productDto;
}
