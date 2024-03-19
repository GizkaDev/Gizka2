package ru.gizka.api.service.fightLogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.fight.Turn;

import java.util.Random;

@Service
@Slf4j
public class TurnLogic {

    private final Random random;

    @Autowired
    public TurnLogic(Random random) {
        this.random = random;
    }

    public Turn simulate(Integer turnNum, Fighter fighter1, Fighter fighter2) {
        log.info("Сервис логики хода рассчитывает ход: {} для героев: {} {}({}) и {} {}({})",
                turnNum,
                fighter1.getName(), fighter1.getLastname(), fighter1.getUserLogin(),
                fighter2.getName(), fighter2.getLastname(), fighter2.getUserLogin());
        Turn turn = determineAttacker(fighter1, fighter2);
        turn.setTurnNum(turnNum);
        determineHit(turn);
        return turn;
    }

    private Turn determineHit(Turn turn) {
        Integer attack = random.nextInt(0, turn.getAttacker().getAttack());
        log.info("Сервис логики хода: атакующий герой: {} {}({}) выбросил атаку: {} из: 0-{}",
                turn.getAttacker().getName(), turn.getAttacker().getLastname(), turn.getAttacker().getUserLogin(), attack, turn.getAttacker().getAttack());

        Integer evasion = random.nextInt(0, turn.getDefender().getEvasion());
        log.info("Сервис логики хода: защищающийся герой: {} {}({}) выбросил уклонение: {} из: 0-{}",
                turn.getDefender().getName(), turn.getDefender().getLastname(), turn.getDefender().getUserLogin(), evasion, turn.getDefender().getEvasion());

        turn.setAttack(attack);
        turn.setEvasion(evasion);

        if (attack > evasion) {
            log.info("Сервис логики хода: атакующий герой: {} {}({}) попал",
                    turn.getAttacker().getName(), turn.getAttacker().getLastname(), turn.getAttacker().getUserLogin());

            Integer physDamage = random.nextInt(1, turn.getAttacker().getPhysDamage());
            log.info("Сервис логики хода: атакующий герой: {} {}({}) наносит урон: {} из: 1-{}",
                    turn.getAttacker().getName(), turn.getAttacker().getLastname(), turn.getAttacker().getUserLogin(), physDamage, turn.getAttacker().getPhysDamage());

            Integer defenderRemainingHp = turn.getDefender().getCurrentHp() - physDamage;
            turn.getDefender().setCurrentHp(defenderRemainingHp);
            log.info("Сервис логики хода: защищающийся герой: {} {}({}) сохраняет здоровье: {}/{}",
                    turn.getDefender().getName(), turn.getDefender().getLastname(), turn.getDefender().getUserLogin(),
                    turn.getDefender().getCurrentHp(), turn.getDefender().getMaxHp());

            turn.setPhysDamage(physDamage);
            turn.setCurrentHp(defenderRemainingHp);
            return turn;
        }

        log.info("Сервис логики хода: атакующий герой: {} {}({}) промахнулся",
                turn.getAttacker().getName(), turn.getAttacker().getLastname(), turn.getAttacker().getUserLogin());
        turn.setPhysDamage(0);
        turn.setCurrentHp(turn.getDefender().getCurrentHp());
        return turn;
    }

    private Turn determineAttacker(Fighter fighter1, Fighter fighter2) {
        Integer init1;
        Integer init2;
        do {
            init1 = random.nextInt(0, fighter1.getInitiative());
            init2 = random.nextInt(0, fighter2.getInitiative());
        }
        while (init1.equals(init2));
        log.info("Сервис логики хода: герой: {} {}({}) выбросил инициативу: {} из: 0-{}",
                fighter1.getName(), fighter1.getLastname(), fighter1.getUserLogin(), init1, fighter1.getInitiative());
        log.info("Сервис логики хода: герой: {} {}({}) выбросил инициативу: {} из: 0-{}",
                fighter2.getName(), fighter2.getLastname(), fighter2.getUserLogin(), init2, fighter2.getInitiative());

        Fighter attacker;
        Fighter defender;
        Integer attackerInit;
        Integer defenderInit;
        if (init1 > init2) {
            attacker = fighter1;
            defender = fighter2;
            attackerInit = init1;
            defenderInit = init2;
//            attacker.setInitiative(attacker.getInitiative() - 1);
        } else {
            attacker = fighter2;
            defender = fighter1;
            attackerInit = init2;
            defenderInit = init1;
//            fighter2.setInitiative(fighter2.getInitiative() - 1);
        }

        log.info("Сервис логики хода: герой: {} {}({}) атакует",
                attacker.getName(), attacker.getLastname(), attacker.getUserLogin());
//        log.info("Сервис логики хода: инициатива атакующего героя: {} {}({}) снижена на 1 до {}",
//                attacker.getName(), attacker.getLastname(), attacker.getUserLogin(), fighter1.getInitiative());
        log.info("Сервис логики хода: герой: {} {}({}) защищается",
                defender.getName(), defender.getLastname(), defender.getUserLogin());

        Turn turn = new Turn();
        turn.setAttacker(attacker);
        turn.setAttackerInit(attackerInit);
        turn.setDefender(defender);
        turn.setDefenderInit(defenderInit);

        return turn;
    }
}
