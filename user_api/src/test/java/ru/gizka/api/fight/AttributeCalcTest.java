package ru.gizka.api.fight;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.model.creature.Creature;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.race.RaceSize;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.AttributeCalculator;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeCalcTest {
    private Fighter heroFighter;
    private Hero hero;
    private AppUser appUser;
    private Race race;
    private Creature creature;

    private final AttributeCalculator attributeCalculator = new AttributeCalculator();


    @Test
    @Description(value = "Тест на расчет атрибутов героя")
    public void testCalculateAttributes() {
        // given
        appUser = new AppUser(0L, "testLogin", null, null, null, null, null);
        race = new Race(0L, "Человек", null, true, null, null, 0, 0, 0, 0, 0, RaceSize.AVERAGE);
        hero = new Hero(1234L, "TestName", "TestLastName",
                9, 10, 11, 10,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null,null,
                new Date(), appUser, Status.ALIVE, Collections.emptyList(), race, null, null, null);

        // when
        attributeCalculator.calculateForNew(hero);

        // then
        assertEquals(10, hero.getMaxAttack());
        assertEquals(10, hero.getMaxEvasion());
        assertEquals(9, hero.getMaxPhysDamage());
        assertEquals(11 * 3, hero.getMaxHp());
        assertEquals(10, hero.getMaxInit());
        assertEquals(11 * 3, hero.getCurrentHp());
        assertEquals(11, hero.getEndurance());
        assertEquals(0, hero.getMinAttack());
        assertEquals(0, hero.getMinEvasion());
        assertEquals(0, hero.getMinPhysDamage());
        assertEquals(0, hero.getMinInit());
        assertEquals(11, hero.getEndurance());
        assertEquals(27000L, hero.getMaxWeight());
        assertEquals(10, hero.getSearch());
        assertEquals(10, hero.getTreat());
        assertEquals(0, hero.getDef());
    }

    @Test
    @Description(value = "Тест на расчет мин. и макс. атрибутов героя")
    public void testCalculateMinMaxAttributes() {
        // given
        appUser = new AppUser(0L, "testLogin", null, null, null, null, null);
        race = new Race(0L, "Человек", null, true, null, null, 0, 0, 0, 0, 0, RaceSize.AVERAGE);
        hero = new Hero(1234L, "TestName", "TestLastName",
                9, 10, 11, 10,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null,
                new Date(), appUser, Status.ALIVE, Collections.emptyList(), race, null, null, null);

        // when
        attributeCalculator.calculateForNew(hero);

        // then
        assertEquals(0, (int) hero.getMinInit());
        assertEquals(0, (int) hero.getMinAttack());
        assertEquals(0, (int) hero.getMinEvasion());
        assertEquals(0, (int) hero.getMinPhysDamage());
        assertEquals(hero.getDex(), (int) hero.getMaxInit());
        assertEquals(hero.getDex(), (int) hero.getMaxAttack());
        assertEquals(hero.getDex(), (int) hero.getMaxEvasion());
        assertEquals(hero.getStr(), (int) hero.getMaxPhysDamage());
        assertEquals(hero.getCon() * 3, (int) hero.getMaxHp());
        assertEquals(hero.getCon() * 3, (int) hero.getCurrentHp());
        assertEquals(hero.getCon(), hero.getEndurance());
        assertEquals(hero.getStr() * 3000L, hero.getMaxWeight());
        assertEquals(hero.getTreat(), hero.getWis());
        assertEquals(hero.getSearch(), hero.getWis());
        assertEquals(hero.getDef(), race.getDefBonus());
    }

    @Test
    @Description(value = "Тест на расчет мин. и макс. атрибутов героя")
    public void testCreatureCalculateMinMaxAttributes() {
        // given
        race = new Race(0L, "Человек", null, true, null, null, 0, 0, 0, 0, 0, RaceSize.AVERAGE);
        creature = new Creature(1234L, "TestName",
                9, 10, 11, new Date(),
                race, null, null, null, null, null, null, null, null, null, null, null, 0,null);

        // when
        attributeCalculator.calculateForNew(creature);

        // then
        assertEquals(0, (int) creature.getMinInit());
        assertEquals(0, (int) creature.getMinAttack());
        assertEquals(0, (int) creature.getMinEvasion());
        assertEquals(0, (int) creature.getMinPhysDamage());
        assertEquals(creature.getDex(), (int) creature.getMaxInit());
        assertEquals(creature.getDex(), (int) creature.getMaxAttack());
        assertEquals(creature.getDex(), (int) creature.getMaxEvasion());
        assertEquals(creature.getStr(), (int) creature.getMaxPhysDamage());
        assertEquals(creature.getCon() * 3, (int) creature.getMaxHp());
        assertEquals(creature.getCon() * 3, (int) creature.getCurrentHp());
        assertEquals(creature.getCon(), creature.getEndurance());
        assertEquals(creature.getDef(), creature.getDef() + race.getDefBonus());
    }
}
