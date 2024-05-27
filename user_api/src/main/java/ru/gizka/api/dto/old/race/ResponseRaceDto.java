package ru.gizka.api.dto.old.race;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.old.race.RaceSize;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRaceDto {
    private Long id;
    private String name;
    private Date createdAt;
    private Boolean isPlayable;
    private Integer strBonus;
    private Integer dexBonus;
    private Integer conBonus;
    private Integer wisBonus;
    private Integer defBonus;
    private RaceSize raceSize;
}
