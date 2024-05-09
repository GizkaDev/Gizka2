package ru.gizka.api.service.fightLogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.fight.Turn;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FightLogic {
    private final TurnLogic turnLogic;

    @Autowired
    public FightLogic(TurnLogic turnLogic) {
        this.turnLogic = turnLogic;
    }

    public List<Turn> simulate(Fighter fighter1, Fighter fighter2) {
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
}
