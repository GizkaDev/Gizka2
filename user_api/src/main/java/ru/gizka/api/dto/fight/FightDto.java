package ru.gizka.api.dto.fight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.creature.ResponseCreatureDto;
import ru.gizka.api.dto.hero.ResponseHeroDto;
import ru.gizka.api.dto.item.ResponseItemDto;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FightDto {
    private Long id;
    private ResponseHeroDto heroFighter;
    private ResponseCreatureDto creatureFighter;
    private List<Turn> turns;
    private String result;
    private Date createdAt;
    private List<ResponseItemDto> loot;
}
