package ru.gizka.api.service.fightLogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.fight.Duel;
import ru.gizka.api.model.fight.Fight;
import ru.gizka.api.model.fight.Result;
import ru.gizka.api.model.hero.Hero;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FightLogic {
    private final TurnLogic turnLogic;
    private final ObjectMapper objectMapper;

    @Autowired
    public FightLogic(TurnLogic turnLogic,
                      ObjectMapper objectMapper) {
        this.turnLogic = turnLogic;
        this.objectMapper = objectMapper;
    }

    public Fight simulate(Hero hero, Creature creature) {
        log.info("Сервис логики сражения готовит компоненты для героя: {} {}({}) и моба: {}",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin(),
                creature.getName());
        Fighter fighter1 = new Fighter(hero);
        List<Turn> turns = simulate(fighter1, new Fighter(creature));
        String turnsAsString = "";
        try {
            turnsAsString = objectMapper.writeValueAsString(turns);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (turns.get(turns.size() - 1).getAttacker().getName().equals(fighter1.getName())) {
            hero.setCurrentHp(turns.get(turns.size() - 1).getAttacker().getCurrentHp());
        } else {
            hero.setCurrentHp(turns.get(turns.size() - 1).getDefender().getCurrentHp());
        }
        return Fight.builder()
                .hero(hero)
                .creature(creature)
                .turns(turnsAsString)
                .result(getResult(turns, fighter1.getName()))
                .createdAt(new Date())
                .build();
    }

    public Duel simulate(Hero hero1, Hero hero2) {
        log.info("Сервис логики сражений готовит компоненты для героев: {} {}({}) и {} {}({})",
                hero1.getName(), hero1.getLastname(), hero1.getAppUser().getLogin(),
                hero2.getName(), hero2.getLastname(), hero2.getAppUser().getLogin());
        Fighter fighter1 = new Fighter(hero1);
        List<Turn> turns = simulate(fighter1, new Fighter(hero2));
        String turnsAsString = "";
        try {
            turnsAsString = objectMapper.writeValueAsString(turns);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Duel.builder()
                .heroes(List.of(hero1, hero2))
                .turns(turnsAsString)
                .result(getResult(turns, fighter1.getName()))
                .createdAt(new Date())
                .build();
    }

    private List<Turn> simulate(Fighter fighter1, Fighter fighter2) {
        log.info("Сервис логики сражений начал симуляцию сражения для бойцов: {} и {}",
                fighter1.getName(), fighter2.getName());
        List<Turn> turns = new ArrayList<>();
        Integer maxTurns = getMaxTurns(fighter1, fighter2);
        Integer turnNum = 1;
        while (fighter1.getCurrentHp() > 0 &&
                fighter2.getCurrentHp() > 0 &&
                turnNum <= maxTurns &&
                (fighter1.getCurrentCon() > 0 || fighter2.getCurrentCon() > 0)) {
            Turn turn = turnLogic.simulate(turnNum, fighter1, fighter2);
            turns.add(turn);
            if (turn.getAttacker().getName().equals(fighter1.getName())) {
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

    private Integer getMaxTurns(Fighter fighter1, Fighter fighter2) {
        Integer turns = Math.max(fighter1.getCurrentCon(), fighter2.getCurrentCon());
        log.info("Сервис логики сражений высчитал количество ходов: {}", turns);
        return turns;
    }

    //Если хп ни у кого не опустилось до нуля и ниже, то ничья
    //Если у кого-то ниже, то побеждает всегда последний атакующий, т.к.
    //он последний наносит урон
    //Логин последнего атакующего == логину первого героя, то он победитель
    //Иначе второй герой
    private Result getResult(List<Turn> turns, String fighter1Name) {
        Fighter lastTurnAttacker = turns.get(turns.size() - 1).getAttacker();
        Fighter lastTurnDefender = turns.get(turns.size() - 1).getDefender();
        Result result;
        if (lastTurnAttacker.getCurrentHp() > 0 && lastTurnDefender.getCurrentHp() > 0) {
            result = Result.DRAW;
            log.info("Сервис логики сражений определил итог: {}", result.getDescription());
        } else if (lastTurnAttacker.getName().equals(fighter1Name)) {
            result = Result.ATTACKER;
            log.info("Сервис логики сражений определил итог: {} {}", result.getDescription(), lastTurnAttacker.getName());
        } else {
            result = Result.DEFENDER;
            log.info("Сервис логики сражений определил итог: {} {}", result.getDescription(), lastTurnAttacker.getName());
        }
        return result;
    }
}
