package ru.gizka.api.service.fightLogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;

@Service
@Slf4j
public class AttributeCalculator {

    public void calculate(Fighter fighter){
        log.info("Калькулятор атрибутов рассчитывает атрибуты для бойца: {} {}({})",
                fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        fighter.setAttack(calculateAttack(fighter));
        fighter.setEvasion(calculateEvasion(fighter));
        fighter.setPhysDamage(calculatePhysDamage(fighter));
        fighter.setMaxHp(calculateMaxHp(fighter));
        fighter.setInitiative(calculateInitiative(fighter));
        fighter.setCurrentHp(calculateCurrentHp(fighter));
    }

    private Integer calculateAttack(Fighter fighter){
        Integer attack = fighter.getDex();
        log.info("Калькулятор атрибутов рассчитал атаку: {} при ловкости: {} для бойца: {} {}({})",
                attack, fighter.getDex(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return attack;
    }

    private Integer calculateEvasion(Fighter fighter){
        Integer evasion =  fighter.getDex();
        log.info("Калькулятор атрибутов рассчитал уклонение: {} при ловкости: {} для бойца: {} {}({})",
                evasion, fighter.getDex(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return evasion;
    }

    private Integer calculatePhysDamage(Fighter fighter){
        Integer physDamage = fighter.getStr();
        log.info("Калькулятор атрибутов рассчитал физ.урон: {}  при силе: {} для бойца: {} {}({})",
                physDamage, fighter.getStr(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return physDamage;
    }

    private Integer calculateMaxHp(Fighter fighter){
        Integer maxHp = fighter.getCon() * 3;
        log.info("Калькулятор атрибутов рассчитал макс. ОЗ: {}  при выносливости: {} для бойца: {} {}({})",
                maxHp, fighter.getCon(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return maxHp;
    }

    private Integer calculateInitiative(Fighter fighter){
        Integer initiative = fighter.getDex();
        log.info("Калькулятор атрибутов рассчитал инициативу: {} при ловкости: {} для бойца: {} {}({})",
                initiative, fighter.getDex(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return initiative;
    }

    private Integer calculateCurrentHp(Fighter fighter){
        Integer currentHp = calculateMaxHp(fighter);
        log.info("Калькулятор атрибутов рассчитал текущие ОЗ: {}  для бойца: {} {}({})",
                currentHp, fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return currentHp;
    }
}
