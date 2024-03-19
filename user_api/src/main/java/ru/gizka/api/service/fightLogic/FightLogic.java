package ru.gizka.api.service.fightLogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.model.fight.Result;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.service.FightService;
import ru.gizka.api.service.HeroService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public Duel simulate(Hero hero1, Hero hero2) {
        log.info("Сервис логики сражения готовит компоненты для героев: {} {}({}) и {} {}({})",
                hero1.getName(), hero1.getLastname(), hero1.getAppUser().getLogin(),
                hero2.getName(), hero2.getLastname(), hero2.getAppUser().getLogin());
        Fighter fighter1 = fighterBuilder.build(hero1);
        Fighter fighter2 = fighterBuilder.build(hero2);
        Duel duel = new Duel();
        duel.setHeroes(new ArrayList<>());
        duel.getHeroes().add(hero1);
        duel.getHeroes().add(hero2);
        try {
            List<Turn> turns = simulate(fighter1, fighter2);
            duel.setTurns(objectMapper.writeValueAsString(turns));
            duel.setResult(getResult(turns, hero1.getAppUser().getLogin()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        duel.setCreatedAt(new Date());
        duel = fightService.save(duel);
        saveRelation(hero1.getId(), hero2.getId(), duel);
        return duel;
    }

    private Integer getMaxTurns(Fighter fighter1, Fighter fighter2) {
        Integer turns = Math.max(fighter1.getCon(), fighter2.getCon());
        log.info("Сервис логики сражения высчитал количество ходов: {}", turns);
        return turns;
    }

    private List<Turn> simulate(Fighter fighter1, Fighter fighter2) {
        log.info("Сервис логики сражения начал симуляцию дуэли для героев: {} {}({}) и {} {}({})",
                fighter1.getName(), fighter1.getLastname(), fighter1.getUserLogin(),
                fighter2.getName(), fighter2.getLastname(), fighter2.getUserLogin());
        List<Turn> turns = new ArrayList<>();
        Integer maxTurns = getMaxTurns(fighter1, fighter2);
        Integer turnNum = 1;
        while (fighter1.getCurrentHp() > 0 && fighter2.getCurrentHp() > 0 && turnNum <= maxTurns) {
            Turn turn = turnLogic.simulate(turnNum, new Fighter(fighter1), new Fighter(fighter2));
            turns.add(turn);
            if (turn.getAttacker().getUserLogin().equals(fighter1.getUserLogin())) {
                fighter1 = turn.getAttacker();
                fighter2 = turn.getDefender();
            } else {
                fighter2 = turn.getAttacker();
                fighter1 = turn.getDefender();
            }
            turnNum++;
        }
        return turns;
    }

    private void saveRelation(Long hero1Id, Long hero2Id, Duel duel) {
        Hero hero1 = heroService.getByIdWithDuels(hero1Id);
        Hero hero2 = heroService.getByIdWithDuels(hero2Id);
        log.info("Сервис логики сражения сохраняет связь герой-дуэль для героев: {} {}({}) и {} {}({})",
                hero1.getName(), hero1.getLastname(), hero1.getAppUser().getLogin(),
                hero2.getName(), hero2.getLastname(), hero2.getAppUser().getLogin());
        hero1.getDuels().add(duel);
        hero2.getDuels().add(duel);
        heroService.save(hero1);
        heroService.save(hero2);
    }

    //Если хп ни у кого не опустилось до нуля и ниже, то ничья
    //Если у кого-то ниже, то побеждает всегда последний атакующий, т.к.
    //он последний наносит урон
    //Логин последнего атакующего == логину первого героя, то он победитель
    //Иначе второй герой
    private Result getResult(List<Turn> turns, String login1) {
        Fighter lastTurnAttacker = turns.get(turns.size() - 1).getAttacker();
        Fighter lastTurnDefender = turns.get(turns.size() - 1).getDefender();
        Result result;
        if (lastTurnAttacker.getCurrentHp() > 0 && lastTurnDefender.getCurrentHp() > 0) {
            result = Result.DRAW;
            log.info("Сервис логики сражения определил итог: {}", result.getDescription());
        } else if (lastTurnAttacker.getUserLogin().equals(login1)) {
            result = Result.ATTACKER;
            log.info("Сервис логики сражения определил итог: {} {} {}({})", result.getDescription(),
                    lastTurnAttacker.getName(), lastTurnAttacker.getLastname(), lastTurnAttacker.getUserLogin());
        } else {
            result = Result.DEFENDER;
            log.info("Сервис логики сражения определил итог: {} {} {}({})", result.getDescription(),
                    lastTurnAttacker.getName(), lastTurnAttacker.getLastname(), lastTurnAttacker.getUserLogin());
        }
        return result;
    }
}
