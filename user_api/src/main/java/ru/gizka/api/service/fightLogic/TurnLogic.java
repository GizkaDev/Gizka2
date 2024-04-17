package ru.gizka.api.service.fightLogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.service.RandomRoller;

@Service
@Slf4j
public class TurnLogic {

    private final RandomRoller randomRoller;

    @Autowired
    public TurnLogic(RandomRoller randomRoller) {
        this.randomRoller = randomRoller;
    }

    public Turn simulate(Integer turnNum, Fighter fighter1, Fighter fighter2) {
        log.info("Сервис логики хода рассчитывает ход: {} для бойцов: {} и {}",
                turnNum, fighter1.getName(), fighter2.getName());
        Turn turn = determineAttacker(fighter1, fighter2);
        turn.setTurnNum(turnNum);
        determineHit(turn);
        determineCurrentCon(turn);
        return turn;
    }

    private void determineCurrentCon(Turn turn) {
        Integer attackerCurrentCon;
        Integer defenderCurrentCon;
        if (turn.getAttacker().getCurrentCon() > 0) {
            attackerCurrentCon = turn.getAttacker().getCurrentCon() - 1;
        } else {
            attackerCurrentCon = 0;
        }
        turn.getAttacker().setCurrentCon(attackerCurrentCon);

        log.info("Сервис логики хода рассчитал текущую выносливость: {} для бойца: {}", attackerCurrentCon, turn.getAttacker().getName());

        if (turn.getDefender().getCurrentCon() > 0) {
            defenderCurrentCon = turn.getDefender().getCurrentCon() - 1;
        } else {
            defenderCurrentCon = 0;
        }
        turn.getDefender().setCurrentCon(defenderCurrentCon);

        log.info("Сервис логики хода рассчитал текущую выносливость: {} для бойца: {}", defenderCurrentCon, turn.getDefender().getName());
    }

    private void determineHit(Turn turn) {
        Integer attack = randomRoller.rollAttack(turn.getAttacker().getMinAttack(), turn.getAttacker().getMaxAttack());
        log.info("Сервис логики хода: атакующий боец: {} выбросил атаку: {} из: {}-{}",
                turn.getAttacker().getName(), attack, turn.getAttacker().getMinAttack(), turn.getAttacker().getMaxAttack());

        Integer evasion = randomRoller.rollEvasion(turn.getDefender().getMinEvasion(), turn.getDefender().getMaxEvasion());
        log.info("Сервис логики хода: защищающийся боец: {} выбросил уклонение: {} из: {}-{}",
                turn.getDefender().getName(), evasion, turn.getDefender().getMinEvasion(), turn.getDefender().getMaxEvasion());

        turn.setAttack(attack);
        turn.setEvasion(evasion);

        if (attack > evasion) {
            log.info("Сервис логики хода: атакующий боец: {} попал", turn.getAttacker().getName());

            Integer physDamage = randomRoller.rollPhysDamage(turn.getAttacker().getMinPhysDamage(), turn.getAttacker().getMaxPhysDamage());
            log.info("Сервис логики хода: атакующий боец: {} наносит урон: {} из: {}-{}",
                    turn.getAttacker().getName(), physDamage, turn.getAttacker().getMinPhysDamage(), turn.getAttacker().getMaxPhysDamage());

            Integer defenderRemainingHp = turn.getDefender().getCurrentHp() - physDamage;
            turn.getDefender().setCurrentHp(defenderRemainingHp);
            log.info("Сервис логики хода: защищающийся боец: {} сохраняет здоровье: {}/{}",
                    turn.getDefender().getName(), turn.getDefender().getCurrentHp(), turn.getDefender().getMaxHp());

            turn.setPhysDamage(physDamage);
            turn.setCurrentHp(defenderRemainingHp);
            return;
        }

        log.info("Сервис логики хода: атакующий боец: {} промахнулся", turn.getAttacker().getName());
        turn.setPhysDamage(0);
        turn.setCurrentHp(turn.getDefender().getCurrentHp());
    }

    private Turn determineAttacker(Fighter fighter1, Fighter fighter2) {
        Integer init1;
        Integer init2;
        Fighter attacker;
        Fighter defender;
        Integer attackerInit;
        Integer defenderInit;
        if (fighter1.getCurrentCon() > 0 && fighter2.getCurrentCon() > 0) {
            do {
                init1 = randomRoller.rollInitiative(fighter1.getMinInit(), fighter1.getMaxInit());
                init2 = randomRoller.rollInitiative(fighter2.getMinInit(), fighter2.getMaxInit());
            }
            while (init1.equals(init2));
            log.info("Сервис логики хода: боец: {} выбросил инициативу: {} из: {}-{}",
                    fighter1.getName(), init1, fighter1.getMinInit(), fighter1.getMaxInit());
            log.info("Сервис логики хода: боец: {} выбросил инициативу: {} из: {}-{}",
                    fighter2.getName(), init2, fighter2.getMinInit(), fighter2.getMaxInit());

            if (init1 > init2) {
                attacker = fighter1;
                defender = fighter2;
                attackerInit = init1;
                defenderInit = init2;
            } else {
                attacker = fighter2;
                defender = fighter1;
                attackerInit = init2;
                defenderInit = init1;
            }
        } else {
            attacker = fighter1.getCurrentCon() > 0 ? fighter1 : fighter2;
            defender = fighter2.getCurrentCon() == 0 ? fighter2 : fighter1;
            attackerInit = 0;
            defenderInit = 0;
            log.info("Сервис логики хода: боец: {} имеет запас сил 0", defender.getName());
        }

        log.info("Сервис логики хода: боец: {} атакует", attacker.getName());
        log.info("Сервис логики хода: боец: {} защищается", defender.getName());

        Turn turn = new Turn();
        turn.setAttacker(attacker);
        turn.setAttackerInit(attackerInit);
        turn.setDefender(defender);
        turn.setDefenderInit(defenderInit);

        return Turn.builder()
                .attacker(attacker)
                .defender(defender)
                .attackerInit(attackerInit)
                .defenderInit(defenderInit)
                .build();
    }
}
