package ru.gizka.api.dto.old.fight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gizka.api.dto.old.creature.ResponseCreatureDto;
import ru.gizka.api.dto.old.hero.ResponseHeroDto;
import ru.gizka.api.dto.old.item.ResponseItemDto;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FightDto {
    private Long id;
    private ResponseHeroDto heroFighter;
    private ResponseCreatureDto creatureFighter;
    private List<Turn> turns;
    private String result;
    private Date createdAt;
    private List<ResponseItemDto> loot;
}
