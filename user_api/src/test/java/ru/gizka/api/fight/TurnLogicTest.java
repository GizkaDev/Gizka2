package ru.gizka.api.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.race.RaceSize;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.AttributeCalculator;
import ru.gizka.api.service.fightLogic.TurnLogic;
import ru.gizka.api.util.RandomRoller;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class TurnLogicTest {
    private TurnLogic turnLogic;
    private AttributeCalculator attributeCalculator;
    private Hero hero1;
    private Hero hero2;
    private AppUser appUser1;
    private AppUser appUser2;
    private Fighter heroFighter1;
    private Fighter heroFighter2;
    private Race race;

    @BeforeEach
    void setUp() {
        this.turnLogic = new TurnLogic(new RandomRoller(new Random()));
        this.attributeCalculator = new AttributeCalculator();
        appUser1 = new AppUser(0L, "testLogin", null, null, null, null, null);
        race = new Race(0L, "Человек", null, true, null, null, 0, 0, 0, 0, 0, RaceSize.AVERAGE);
        hero1 = new Hero(1234L, "TestName", "TestLastName",
                9, 10, 11, 10,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null,
                null,
                new Date(), appUser1, Status.ALIVE, Collections.emptyList(), race, null, null, null);
        appUser2 = new AppUser(0L, "testLogin2", null, null, null, null, null);
        hero2 = new Hero(1234L, "TestName2", "TestLastName2",
                4, 5, 11, 10,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null,
                null,
                new Date(), appUser2, Status.ALIVE, Collections.emptyList(), race, null, null, null);
        attributeCalculator.calculateForNew(hero1);
        attributeCalculator.calculateForNew(hero2);
        heroFighter1 = new Fighter(hero1);
        heroFighter2 = new Fighter(hero2);
    }

    @Test
    @Description(value = "Тест на определение атакующего")
    void testDetermineAttacker() throws Exception {
        //given
        Class<?> clazz = turnLogic.getClass();
        Method method = clazz.getDeclaredMethod("determineAttacker", Fighter.class, Fighter.class);
        method.setAccessible(true);
        int fighter1A = 0;
        int fighter2A = 0;
        //when
        for (int i = 0; i < 1000; i++) {
            Turn turn = (Turn) method.invoke(turnLogic, heroFighter1, heroFighter2);
            if (turn.getAttacker().equals(heroFighter1)) {
                fighter1A++;
            } else {
                fighter2A++;
            }
        }
        assertTrue(fighter1A > fighter2A);
    }

    @Test
    @Description(value = "Тест на определение атакующего при 0 текущей выносливости у одного из бойцов")
    void testDetermineAttackerWhenCurrentCon0() throws Exception {
        //given
        heroFighter1.setEndurance(0);
        Class<?> clazz = turnLogic.getClass();
        Method method = clazz.getDeclaredMethod("determineAttacker", Fighter.class, Fighter.class);
        method.setAccessible(true);
        int fighter1A = 0;
        int fighter2A = 0;
        //when
        for (int i = 0; i < 1000; i++) {
            Turn turn = (Turn) method.invoke(turnLogic, heroFighter1, heroFighter2);
            if (turn.getAttacker().equals(heroFighter1)) {
                fighter1A++;
            } else {
                fighter2A++;
            }
        }
        assertEquals(0, fighter1A);
        assertEquals(1000, fighter2A);
    }

    @Test
    @Description(value = "Тест на бросок инициативы в диапазоне")
    void testDetermineAttackerInitiativeSpread() throws Exception {
        //given
        Class<?> clazz = turnLogic.getClass();
        Method method = clazz.getDeclaredMethod("determineAttacker", Fighter.class, Fighter.class);
        method.setAccessible(true);
        //when
        for (int i = 0; i < 1000; i++) {
            Turn turn = (Turn) method.invoke(turnLogic, heroFighter1, heroFighter2);
            //then
            assertTrue(turn.getAttackerInit() >= 0);
            assertTrue(turn.getAttackerInit() <= turn.getAttacker().getDex());
            assertTrue(turn.getDefenderInit() >= 0);
            assertTrue(turn.getDefenderInit() <= turn.getDefender().getDex());
        }
    }

    @Test
    @Description(value = "Тест на бросок атаки, уклонения и урона в диапазоне. " +
            "Так же проверка на количество урона при промахе." +
            "Проверка на вычитание hp")
    void testDetermineHitTest() throws Exception {
        //given
        Class<?> clazz = turnLogic.getClass();
        Method method = clazz.getDeclaredMethod("determineHit", Turn.class);
        method.setAccessible(true);
        Turn turn = new Turn();
        turn.setAttacker(heroFighter1);
        turn.setDefender(heroFighter2);
        //when
        for (int i = 0; i < 1000; i++) {
            Integer hpBeforeTurn = turn.getDefender().getCurrentHp();
            method.invoke(turnLogic, turn);
            assertTrue(turn.getAttack() >= 0);
            assertTrue(turn.getAttack() <= turn.getAttacker().getDex());
            assertTrue(turn.getEvasion() >= 0);
            assertTrue(turn.getEvasion() <= turn.getDefender().getDex());
            if (turn.getAttack() > turn.getEvasion()) {
                assertTrue(turn.getPhysDamage() >= 0);
                assertTrue(turn.getPhysDamage() <= turn.getAttacker().getStr());
                assertEquals(turn.getCurrentHp(), turn.getDefender().getCurrentHp());
                assertEquals(turn.getCurrentHp() + turn.getRealDamage(), hpBeforeTurn);
            } else {
                assertEquals(0, (int) turn.getRealDamage());
                assertEquals(turn.getCurrentHp(), hpBeforeTurn);
            }
        }
    }

    @Test
    @Description(value = "Тест на учитыванием защиты")
    void testDetermineHitTest_WithDef() throws Exception {
        //given
        Class<?> clazz = turnLogic.getClass();
        Method method = clazz.getDeclaredMethod("determineHit", Turn.class);
        method.setAccessible(true);
        Turn turn = new Turn();
        turn.setAttacker(heroFighter1);
        heroFighter2.setDef(10);
        turn.setDefender(heroFighter2);
        //when
        for (int i = 0; i < 1000; i++) {
            Integer hpBeforeTurn = turn.getDefender().getCurrentHp();
            method.invoke(turnLogic, turn);
            assertTrue(turn.getAttack() >= 0);
            assertTrue(turn.getAttack() <= turn.getAttacker().getDex());
            assertTrue(turn.getEvasion() >= 0);
            assertTrue(turn.getEvasion() <= turn.getDefender().getDex());
            if (turn.getAttack() > turn.getEvasion()) {
                assertTrue(turn.getPhysDamage() >= 0);
                assertTrue(turn.getPhysDamage() <= turn.getAttacker().getStr());
                assertEquals(turn.getCurrentHp(), turn.getDefender().getCurrentHp());
                assertEquals(turn.getCurrentHp() + turn.getRealDamage(), hpBeforeTurn);
                assertEquals((int) turn.getRealDamage(), Math.max(0, turn.getPhysDamage() - turn.getDefender().getDef()));
                assertTrue(turn.getDef() != 0);
                assertTrue(turn.getDefender().getDef() != 0);
            } else {
                assertEquals(0, (int) turn.getRealDamage());
                assertEquals(turn.getCurrentHp(), hpBeforeTurn);
            }
        }
    }

    @Test
    @Description(value = "Тест на высчитывание текущей выносливости во время хода и при 0")
    void testDetermineCurrentConTest() throws Exception {
        //given
        Class<?> clazz = turnLogic.getClass();
        Method method = clazz.getDeclaredMethod("determineCurrentCon", Turn.class);
        method.setAccessible(true);
        Turn turn = new Turn();
        turn.setAttacker(heroFighter1);
        turn.setDefender(heroFighter2);
        //when
        for (int i = 0; i < 50; i++) {
            int currentConBefore1 = turn.getAttacker().getEndurance();
            int currentConBefore2 = turn.getDefender().getEndurance();
            method.invoke(turnLogic, turn);
            if (currentConBefore1 > 0) {
                assertEquals(1, currentConBefore1 - turn.getAttacker().getEndurance());
            } else {
                assertEquals(0, turn.getAttacker().getEndurance());
            }
            if (currentConBefore2 > 0) {
                assertEquals(1, currentConBefore2 - turn.getDefender().getEndurance());
            } else {
                assertEquals(0, turn.getDefender().getEndurance());
            }
        }
    }

    @Test
    @Description(value = "Тест на то, что ход правильно сформировался")
    void testTurnSimulationWellFormed() throws Exception {
        //when
        for (int i = 1; i < 10; i++) {
            Turn turn = turnLogic.simulate(i, heroFighter1, heroFighter2);
            assertEquals(i, turn.getTurnNum());
            assertNotNull(turn.getAttacker());
            assertNotNull(turn.getDefender());
            assertNotNull(turn.getAttackerInit());
            assertNotNull(turn.getDefenderInit());
            assertNotNull(turn.getAttack());
            assertNotNull(turn.getEvasion());
            assertNotNull(turn.getCurrentHp());
            assertNotNull(turn.getDef());
            if (turn.getAttack() > turn.getEvasion()) {
                assertNotNull(turn.getPhysDamage());
                assertNotNull(turn.getRealDamage());
            } else {
                assertEquals(0, turn.getPhysDamage());
            }
        }
    }
}
