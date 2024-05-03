package ru.gizka.api.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseProductDto {
    private Long id;
    private String name;
    private Integer price;
    private Long amount;
}
