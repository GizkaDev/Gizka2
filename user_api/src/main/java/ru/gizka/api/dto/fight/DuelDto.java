package ru.gizka.api.dto.fight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DuelDto {
    private Long id;
    private List<Fighter> fighters;
    private List<Turn> turns;
    private String result;
    private Date createdAt;
}
