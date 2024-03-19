package ru.gizka.api.service.fightLogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.service.FightService;
import ru.gizka.api.service.HeroService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FightLogic {
    private final FighterBuilder fighterBuilder;
    private final TurnLogic turnLogic;
    private final FightService fightService;
    private final ObjectMapper objectMapper;
    private final HeroService heroService;

    @Autowired
    public FightLogic(FighterBuilder fighterBuilder,
                      TurnLogic turnLogic,
                      FightService fightService,
                      ObjectMapper objectMapper,
                      HeroService heroService) {
        this.fighterBuilder = fighterBuilder;
        this.turnLogic = turnLogic;
        this.fightService = fightService;
        this.objectMapper = objectMapper;
        this.heroService = heroService;
    }

    public Fight simulate(Hero hero1, Hero hero2) {
        log.info("Сервис логики сражения готовит компоненты для героев: {} {}({}) и {} {}({})",
                hero1.getName(), hero1.getLastname(), hero1.getAppUser().getLogin(),
                hero2.getName(), hero2.getLastname(), hero2.getAppUser().getLogin());
        Fighter fighter1 = fighterBuilder.build(hero1);
        Fighter fighter2 = fighterBuilder.build(hero2);
        Fight fight = new Fight();
        fight.setHeroes(new ArrayList<>());
        fight.getHeroes().add(hero1);
        fight.getHeroes().add(hero2);
        try {
            fight.setTurns(objectMapper.writeValueAsString(simulate(fighter1, fighter2)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        fight.setCreatedAt(new Date());
        fight = fightService.save(fight);
        saveRelation(hero1.getId(), hero2.getId(), fight);
        return fight;
    }

    private Integer getMaxTurns(Fighter fighter1, Fighter fighter2) {
        Integer turns = Math.max(fighter1.getCon(), fighter2.getCon());
        log.info("Сервис логики сражения высчитал количество ходов: {}", turns);
        return turns;
    }

    private List<Turn> simulate(Fighter fighter1, Fighter fighter2) {
        List<Turn> turns = new ArrayList<>();
        Integer maxTurns = getMaxTurns(fighter1, fighter2);
        Integer turnNum = 1;
        while (fighter1.getCurrentHp() > 0 && fighter2.getCurrentHp() > 0 && turnNum <= maxTurns) {
            turns.add(turnLogic.simulate(turnNum, fighter1, fighter2));
            turnNum++;
        }
        return turns;
    }

    private void saveRelation(Long hero1Id, Long hero2Id, Fight fight){
        Hero hero1 = heroService.getByIdWithFights(hero1Id);
        Hero hero2 = heroService.getByIdWithFights(hero2Id);
        hero1.getFights().add(fight);
        hero2.getFights().add(fight);
        heroService.save(hero1);
        heroService.save(hero2);
    }
}
