package ru.gizka.api.dto.old.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProductDto {
    private Long id;
    private String name;
    private Long price;
}
