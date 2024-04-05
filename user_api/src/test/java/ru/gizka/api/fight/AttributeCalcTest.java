package ru.gizka.api.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.service.fightLogic.AttributeCalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeCalcTest {
    private Fighter fighter;

    private final AttributeCalculator attributeCalculator = new AttributeCalculator();

    @BeforeEach
    void setUp() {
        fighter = new Fighter();
    }

    @Test
    @Description(value = "Тест на расчет атрибутов героя")
    public void testCalculateAttributes() {
        // given
        fighter.setStr(9);
        fighter.setDex(10);
        fighter.setCon(11);

        // when
        attributeCalculator.calculate(fighter);

        // then
        assertEquals(10, fighter.getAttack());
        assertEquals(10, fighter.getEvasion());
        assertEquals(9, fighter.getPhysDamage());
        assertEquals(11 * 3, fighter.getMaxHp());
        assertEquals(10, fighter.getInitiative());
        assertEquals(11*3, fighter.getCurrentHp());
        assertEquals(11, fighter.getCurrentCon());
    }
}
