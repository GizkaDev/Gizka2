package ru.gizka.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.hero.Hero;

@Service
@Slf4j
public class AttributeCalculator {

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
        hero.setEndurance(calculateEndurance(hero));
        hero.setMaxWeight(calculateMaxWeight(hero));
        hero.setSearch(calculateSearch(hero));
        hero.setTreat(calculateTreat(hero));
        hero.setDef(calculateDef(hero));
    }

    public void calculateForNew(Creature creature) {
        log.info("Калькулятор атрибутов рассчитывает атрибуты для моба: {}",
                creature.getName());
        creature.setMinInit(calculateMinInit(creature));
        creature.setMaxInit(calculateMaxInit(creature));
        creature.setMinAttack(calculateMinAttack(creature));
        creature.setMaxAttack(calculateMaxAttack(creature));
        creature.setMinEvasion(calculateMinEvasion(creature));
        creature.setMaxEvasion(calculateMaxEvasion(creature));
        creature.setMinPhysDamage(calculateMinPhysDamage(creature));
        creature.setMaxPhysDamage(calculateMaxPhysDamage(creature));
        creature.setMaxHp(calculateMaxHp(creature));
        creature.setCurrentHp(calculateCurrentHpForNew(creature));
        creature.setEndurance(calculateEndurance(creature));
        creature.setDef(calculateDef(creature));
    }

    public Integer calculateDef(Creature creature) {
        Integer def = creature.getDef() + creature.getRace().getDefBonus();
        log.info("Калькулятор атрибутов рассчитал защиту: {} для моба: {}",
                def, creature.getName());
        return def;
    }

    public Integer calculateEndurance(Creature creature) {
        Integer currentCon = creature.getCon();
        log.info("Калькулятор атрибутов рассчитал выносливость: {} при телосложении: {} для моба: {}",
                currentCon, creature.getCon(), creature.getName());
        return currentCon;
    }

    public Integer calculateCurrentHpForNew(Creature creature) {
        Integer maxHp = creature.getCon() * 3;
        log.info("Калькулятор атрибутов рассчитал текущее ОЗ: {}  при телосложении: {} для моба: {}",
                maxHp, creature.getCon(), creature.getName());
        return maxHp;
    }

    public Integer calculateMaxHp(Creature creature) {
        Integer maxHp = creature.getCon() * 3;
        log.info("Калькулятор атрибутов рассчитал макс. ОЗ: {}  при телосложении: {} для моба: {}",
                maxHp, creature.getCon(), creature.getName());
        return maxHp;
    }

    public Integer calculateMaxPhysDamage(Creature creature) {
        int physDamage = creature.getStr();
        log.info("Калькулятор атрибутов рассчитал макс. физ. урон: {} при силе: {} для моба: {}",
                physDamage, creature.getStr(), creature.getName());
        return physDamage;
    }

    public Integer calculateMinPhysDamage(Creature creature) {
        int physDamage = 0;
        log.info("Калькулятор атрибутов рассчитал мин. физ. урон: {} при силе: {} для моба: {}",
                physDamage, creature.getStr(), creature.getName());
        return physDamage;
    }

    public Integer calculateMaxEvasion(Creature creature) {
        int evasion = creature.getDex();
        log.info("Калькулятор атрибутов рассчитал макс. уклонение: {} при ловкости: {} для моба: {}",
                evasion, creature.getDex(), creature.getName());
        return evasion;
    }

    public Integer calculateMinEvasion(Creature creature) {
        int evasion = 0;
        log.info("Калькулятор атрибутов рассчитал мин. уклонение: {} при ловкости: {} для моба: {}",
                evasion, creature.getDex(), creature.getName());
        return evasion;
    }

    public Integer calculateMaxAttack(Creature creature) {
        int attack = creature.getDex();
        log.info("Калькулятор атрибутов рассчитал макс. атаку: {} при ловкости: {} для моба: {}",
                attack, creature.getDex(), creature.getName());
        return attack;
    }

    public Integer calculateMinAttack(Creature creature) {
        int attack = 0;
        log.info("Калькулятор атрибутов рассчитал мин. атаку: {} при ловкости: {} для моба: {}",
                attack, creature.getDex(), creature.getName());
        return attack;
    }

    public Integer calculateMaxInit(Creature creature) {
        int initiative = creature.getDex();
        log.info("Калькулятор атрибутов рассчитал макс. инициативу: {} при ловкости: {} для моба: {}",
                initiative, creature.getDex(), creature.getName());
        return initiative;
    }

    public Integer calculateMinInit(Creature creature) {
        int initiative = 0;
        log.info("Калькулятор атрибутов рассчитал мин. инициативу: {} при ловкости: {} для моба: {}",
                initiative, creature.getDex(), creature.getName());
        return initiative;
    }

    public Integer calculateDef(Hero hero) {
        Integer def = hero.getRace().getDefBonus();
        log.info("Калькулятор атрибутов рассчитал защиту: {} для героя: {} {}({})",
                def, hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return def;
    }

    public Integer calculateTreat(Hero hero) {
        Integer treat = hero.getWis();
        log.info("Калькулятор атрибутов рассчитал навык лечения: {} при мудрости: {} для героя: {} {}({})",
                treat, hero.getStr(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return treat;
    }

    public Integer calculateSearch(Hero hero) {
        Integer search = hero.getWis();
        log.info("Калькулятор атрибутов рассчитал навык обыска: {} при мудрости: {} для героя: {} {}({})",
                search, hero.getStr(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return search;
    }

    public Long calculateMaxWeight(Hero hero) {
        Long maxWeight = hero.getStr() * 3 * 1000L;
        log.info("Калькулятор атрибутов рассчитал макс. вес: {} при силе: {} для героя: {} {}({})",
                maxWeight, hero.getStr(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return maxWeight;
    }

    public Integer calculateEndurance(Hero hero) {
        Integer currentCon = hero.getCon();
        log.info("Калькулятор атрибутов рассчитал выносливость: {} при телосложении: {} для героя: {} {}({})",
                currentCon, hero.getCon(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return currentCon;
    }

    public Integer calculateCurrentHpForNew(Hero hero) {
        Integer maxHp = hero.getCon() * 3;
        log.info("Калькулятор атрибутов рассчитал текущее ОЗ: {}  при телосложении: {} для героя: {} {}({})",
                maxHp, hero.getCon(), hero.getName(), hero.getLastname(), hero.getAppUser().getLogin());
        return maxHp;
    }

    public Integer calculateMaxHp(Hero hero) {
        Integer maxHp = hero.getCon() * 3;
        log.info("Калькулятор атрибутов рассчитал макс. ОЗ: {}  при телосложении: {} для героя: {} {}({})",
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
}
