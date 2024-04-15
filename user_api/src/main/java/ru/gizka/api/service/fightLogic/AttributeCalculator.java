package ru.gizka.api.service.fightLogic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.model.hero.Hero;

@Service
@Slf4j
public class AttributeCalculator {

    public void calculate(Fighter fighter) {
        log.info("Калькулятор атрибутов рассчитывает атрибуты для бойца: {} {}({})",
                fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        fighter.setAttack(calculateAttack(fighter));
        fighter.setEvasion(calculateEvasion(fighter));
        fighter.setPhysDamage(calculatePhysDamage(fighter));
        fighter.setMaxHp(calculateMaxHp(fighter));
        fighter.setInitiative(calculateInitiative(fighter));
        fighter.setCurrentHp(calculateCurrentHp(fighter));
        fighter.setCurrentCon(calculateCurrentCon(fighter));
    }

    public void calculateForNew(Hero hero) {
        log.info("Калькулятор атрибутов рассчитывает атрибуты для нового героя: {} {}({})",
                hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        hero.setMinInit(calculateMinInit(hero));
        hero.setMaxInit(calculateMaxInit(hero));
        hero.setMinAttack(calculateMinAttack(hero));
        hero.setMaxAttack(calculateMaxAttack(hero));
        hero.setMinEvasion(calculateMinEvasion(hero));
        hero.setMaxEvasion(calculateMaxEvasion(hero));
        hero.setMinPhysDamage(calculateMinPhysDamage(hero));
        hero.setMaxPhysDamage(calculateMaxPhysDamage(hero));
        hero.setMaxHp(calculateMaxHp(hero));
        hero.setCurrentHp(calculateCurrentHpForNew(hero));
    }

    public Integer calculateCurrentHpForNew(Hero hero){
        Integer maxHp = hero.getCon() * 3;
        log.info("Калькулятор атрибутов рассчитал текущее ОЗ: {}  при выносливости: {} для героя: {} {}({})",
                maxHp, hero.getCon(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return maxHp;
    }

    public Integer calculateMaxHp(Hero hero){
        Integer maxHp = hero.getCon() * 3;
        log.info("Калькулятор атрибутов рассчитал макс. ОЗ: {}  при выносливости: {} для героя: {} {}({})",
                maxHp, hero.getCon(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return maxHp;
    }

    public Integer calculateMaxPhysDamage(Hero hero) {
        int physDamage = hero.getStr();
        log.info("Калькулятор атрибутов рассчитал макс. физ. урон: {} при силе: {} для героя: {} {}({})",
                physDamage, hero.getStr(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return physDamage;
    }

    public Integer calculateMinPhysDamage(Hero hero) {
        int physDamage = 0;
        log.info("Калькулятор атрибутов рассчитал мин. физ. урон: {} при силе: {} для героя: {} {}({})",
                physDamage, hero.getStr(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return physDamage;
    }

    public Integer calculateMaxEvasion(Hero hero) {
        int evasion = hero.getDex();
        log.info("Калькулятор атрибутов рассчитал макс. уклонение: {} при ловкости: {} для героя: {} {}({})",
                evasion, hero.getDex(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return evasion;
    }

    public Integer calculateMinEvasion(Hero hero) {
        int evasion = 0;
        log.info("Калькулятор атрибутов рассчитал мин. уклонение: {} при ловкости: {} для героя: {} {}({})",
                evasion, hero.getDex(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return evasion;
    }

    public Integer calculateMaxAttack(Hero hero) {
        int attack = hero.getDex();
        log.info("Калькулятор атрибутов рассчитал макс. атаку: {} при ловкости: {} для героя: {} {}({})",
                attack, hero.getDex(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return attack;
    }

    public Integer calculateMinAttack(Hero hero) {
        int attack = 0;
        log.info("Калькулятор атрибутов рассчитал мин. атаку: {} при ловкости: {} для героя: {} {}({})",
                attack, hero.getDex(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return attack;
    }

    public Integer calculateMaxInit(Hero hero) {
        int initiative = hero.getDex();
        log.info("Калькулятор атрибутов рассчитал макс. инициативу: {} при ловкости: {} для героя: {} {}({})",
                initiative, hero.getDex(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return initiative;
    }

    public Integer calculateMinInit(Hero hero) {
        int initiative = 0;
        log.info("Калькулятор атрибутов рассчитал мин. инициативу: {} при ловкости: {} для героя: {} {}({})",
                initiative, hero.getDex(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return initiative;
    }

    private Integer calculateAttack(Fighter fighter) {
        Integer attack = fighter.getDex();
        log.info("Калькулятор атрибутов рассчитал атаку: {} при ловкости: {} для бойца: {} {}({})",
                attack, fighter.getDex(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return attack;
    }

    private Integer calculateEvasion(Fighter fighter) {
        Integer evasion = fighter.getDex();
        log.info("Калькулятор атрибутов рассчитал уклонение: {} при ловкости: {} для бойца: {} {}({})",
                evasion, fighter.getDex(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return evasion;
    }

    private Integer calculatePhysDamage(Fighter fighter) {
        Integer physDamage = fighter.getStr();
        log.info("Калькулятор атрибутов рассчитал физ.урон: {}  при силе: {} для бойца: {} {}({})",
                physDamage, fighter.getStr(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return physDamage;
    }

    private Integer calculateMaxHp(Fighter fighter) {
        Integer maxHp = fighter.getCon() * 3;
        log.info("Калькулятор атрибутов рассчитал макс. ОЗ: {}  при выносливости: {} для бойца: {} {}({})",
                maxHp, fighter.getCon(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return maxHp;
    }

    private Integer calculateInitiative(Fighter fighter) {
        Integer initiative = fighter.getDex();
        log.info("Калькулятор атрибутов рассчитал инициативу: {} при ловкости: {} для бойца: {} {}({})",
                initiative, fighter.getDex(), fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return initiative;
    }

    private Integer calculateCurrentHp(Fighter fighter) {
        Integer currentHp = calculateMaxHp(fighter);
        log.info("Калькулятор атрибутов рассчитал текущие ОЗ: {}  для бойца: {} {}({})",
                currentHp, fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return currentHp;
    }

    private Integer calculateCurrentCon(Fighter fighter) {
        Integer currentCon = fighter.getCon();
        log.info("Калькулятор атрибутов рассчитал текущую выносливость: {}  для бойца: {} {}({})",
                currentCon, fighter.getName(), fighter.getLastname(), fighter.getUserLogin());
        return currentCon;
    }
}
