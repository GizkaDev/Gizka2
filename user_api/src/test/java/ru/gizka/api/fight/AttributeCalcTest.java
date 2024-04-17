package ru.gizka.api.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.fightLogic.AttributeCalculator;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttributeCalcTest {
    private Fighter heroFighter;
    private Hero hero;
    private AppUser appUser;
    private Race race;

    private final AttributeCalculator attributeCalculator = new AttributeCalculator();


    @Test
    @Description(value = "Тест на расчет атрибутов героя")
    public void testCalculateAttributes() {
        // given
        appUser = new AppUser(0L, "testLogin", null, null, null, null, null);
        race = new Race(0L, "Человек", null, true, null, null);
        hero = new Hero(1234L, "TestName", "TestLastName",
                9, 10, 11, new Date(),
                appUser,
                Status.ALIVE,
                Collections.emptyList(),
                race, null, null, null, null, null, null, null, null, null, null, null, null);
        attributeCalculator.calculateForNew(hero);

        // when
        attributeCalculator.calculateForNew(hero);

        // then
        assertEquals(10, hero.getMaxAttack());
        assertEquals(10, hero.getMaxEvasion());
        assertEquals(9, hero.getMaxPhysDamage());
        assertEquals(11 * 3, hero.getMaxHp());
        assertEquals(10, hero.getMaxInit());
        assertEquals(11 * 3, hero.getCurrentHp());
        assertEquals(11, hero.getCurrentCon());
        assertEquals(0, hero.getMinAttack());
        assertEquals(0, hero.getMinEvasion());
        assertEquals(0, hero.getMinPhysDamage());
        assertEquals(0, hero.getMinInit());
        assertEquals(11, hero.getCurrentCon());
    }

    @Test
    @Description(value = "Тест на расчет мин. и макс. атрибутов героя")
    public void testCalculateMinMaxAttributes() {
        // given
        appUser = new AppUser(0L, "testLogin", null, null, null, null, null);
        race = new Race(0L, "Человек", null, true, null, null);
        hero = new Hero(1234L, "TestName", "TestLastName",
                9, 10, 11, new Date(),
                appUser,
                Status.ALIVE,
                Collections.emptyList(),
                race, null, null, null, null, null, null, null, null, null, null, null, null);

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
        assertEquals(hero.getCon(), hero.getCurrentCon());
    }
}
