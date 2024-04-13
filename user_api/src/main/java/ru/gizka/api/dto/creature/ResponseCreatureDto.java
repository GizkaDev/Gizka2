package ru.gizka.api.dto.creature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseCreatureDto {
    private Long id;
    private String name;
    private Integer str;
    private Integer dex;
    private Integer con;
    private Date createdAt;
    private String race;
}
