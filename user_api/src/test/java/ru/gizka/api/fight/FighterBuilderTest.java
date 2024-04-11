package ru.gizka.api.fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Description;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.user.AppUser;
import ru.gizka.api.service.fightLogic.AttributeCalculator;
import ru.gizka.api.service.fightLogic.FighterBuilder;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FighterBuilderTest {

    private Hero hero;
    private AppUser appUser;
    private Race race;
    private final FighterBuilder fighterBuilder = new FighterBuilder(
            new ModelMapper(),
            new AttributeCalculator());

    @BeforeEach
    void setUp() {
        appUser = new AppUser(0L, "testLogin", null, null, null, null, null);
        race = new Race(0L, "Человек", null, true, null);
        hero = new Hero(1234L, "TestName", "TestLastName",
                9, 10, 11, new Date(),
                appUser,
                Status.ALIVE,
                Collections.emptyList(),
                race);

    }

    @Test
    @Description(value = "Тест на сборку бойца для сражения")
    public void testBuildFighter() {
        // given
        //when
        Fighter fighter = fighterBuilder.build(hero);
        //then
        assertEquals(hero.getName(), fighter.getName());
        assertEquals(hero.getLastname(), fighter.getLastname());
        assertEquals(hero.getCreatedAt(), fighter.getCreatedAt());
        assertEquals(appUser.getLogin(), fighter.getUserLogin());
        assertEquals(hero.getStatus().name(), fighter.getStatus());
    }
}
