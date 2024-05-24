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
public class DuelDto {
    private Long id;
    private List<Fighter> heroFighters;
    private List<Turn> turns;
    private String result;
    private Date createdAt;
}
