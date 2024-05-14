package ru.gizka.api.dto.race;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.model.race.RaceSize;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
