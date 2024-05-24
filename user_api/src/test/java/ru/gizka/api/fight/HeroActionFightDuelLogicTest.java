package ru.gizka.api.fight;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.RequestParentTest;
import ru.gizka.api.dto.creature.RequestCreatureDto;
import ru.gizka.api.dto.fight.DuelDto;
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.model.fight.Result;
import ru.gizka.api.model.hero.Hero;
import ru.gizka.api.model.hero.Status;
import ru.gizka.api.model.race.Race;
import ru.gizka.api.model.race.RaceSize;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.service.AttributeCalculator;
import ru.gizka.api.service.fightLogic.FightLogic;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class HeroActionFightDuelLogicTest extends RequestParentTest {
    private FightLogic duelLogic;
    private Fighter heroFighter1;
    private Fighter heroFighter2;
    private RequestAppUserDto userDto;
    private RequestAppUserDto userDto2;
    private RequestHeroDto heroDto;
    private RequestHeroDto heroDto2;
    private MockHttpServletRequestBuilder requestBuilder;
    private String uri = "/api/user/hero/duel";
    private String token;
    private RequestRaceDto raceDto;
    ;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final AttributeCalculator attributeCalculator;

    @Autowired
    private HeroActionFightDuelLogicTest(MockMvc mockMvc,
                                         ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.attributeCalculator = new AttributeCalculator();
    }

    @BeforeEach
    void setUp() {
        duelLogic = new FightLogic(null);

        heroFighter1 = new Fighter();
        heroFighter1.setCon(15);

        heroFighter2 = new Fighter();
        heroFighter2.setCon(9);

        raceDto = new RequestRaceDto("Человек", true,
                0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

        userDto = new RequestAppUserDto(
                "Biba",
                "Qwerty12345!");

        userDto2 = new RequestAppUserDto(
                "Boba",
                "Qwerty12345!");

        heroDto = new RequestHeroDto(
                "Gizka",
                "Green",
                10,
                8,
                12,
                10,
                "Человек");

        heroDto2 = new RequestHeroDto(
                "Lyakusha",
                "Swamp",
                10,
                12,
                8,
                10,
                "Человек");
    }

    @Test
    @Description(value = "Тест на определение количества раундов")
    void testGetMaxTurns() throws Exception {
        //given
        AppUser appUser = new AppUser(0L, "testLogin", null, null, null, null, null);
        Race race = new Race(0L, "Человек", null, true, null, null, 0, 0, 0, 0, 0, RaceSize.AVERAGE);
        Hero hero1 = new Hero(1234L, "TestName", "TestLastName",
                9, 10, 11, 10,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null,
                null,
                new Date(), appUser, Status.ALIVE, Collections.emptyList(), race, null, null, null);
        attributeCalculator.calculateForNew(hero1);
        heroFighter1 = new Fighter(hero1);
        AppUser appUser2 = new AppUser(0L, "testLogin", null, null, null, null, null);
        Race race2 = new Race(0L, "Ящер", null, true, null, null, 0, 0, 0, 0, 0, RaceSize.AVERAGE);
        Hero hero2 = new Hero(1234L, "TestName", "TestLastName",
                9, 10, 9, 10,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null,
                null,
                new Date(), appUser, Status.ALIVE, Collections.emptyList(), race, null, null, null);
        attributeCalculator.calculateForNew(hero2);
        heroFighter2 = new Fighter(hero2);
        Class<?> clazz = duelLogic.getClass();
        Method method = clazz.getDeclaredMethod("getMaxTurns", Fighter.class, Fighter.class);
        method.setAccessible(true);
        //when
        Integer result = (int) method.invoke(duelLogic, heroFighter1, heroFighter2);
        //then
        assertEquals(Math.max(heroFighter1.getCon(), heroFighter2.getCon()), result);
    }

    @Test
    @Description(value = "Тест на то, что в сражении не проводится больше ходов, чем по логике")
    void numberOfTurnsTest() throws Exception {
        //given
        RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
        RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
        String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
        String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
        RequestParentTest.setAdminRights(mockMvc, token1);
        RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
        RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
        RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);

        token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

        requestBuilder = MockMvcRequestBuilders
                .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", token));

        //when
        for (int i = 0; i < 100; i++) {
            MvcResult result = mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isCreated())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            DuelDto duelDto = objectMapper.readValue(json, DuelDto.class);

            assertTrue(duelDto.getTurns().size() <= Math.max(heroDto.getCon(), heroDto2.getCon()));
        }
    }

    @Test
    @Description(value = "Тест на то, что правильно определяется победитель")
    void getResultTest() throws Exception {
        //given
        RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
        RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
        String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
        String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
        RequestParentTest.setAdminRights(mockMvc, token1);
        RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
        RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
        RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);

        token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

        requestBuilder = MockMvcRequestBuilders
                .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", token));
        //when
        for (int i = 0; i < 100; i++) {
            MvcResult result = mockMvc.perform(requestBuilder)
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            DuelDto duelDto = objectMapper.readValue(json, DuelDto.class);
            //then
            Fighter lastTurnAttacker = duelDto.getTurns().get(duelDto.getTurns().size() - 1).getAttacker();
            Fighter lastTurnDefender = duelDto.getTurns().get(duelDto.getTurns().size() - 1).getDefender();
            Result duelResult;
            if (lastTurnAttacker.getCurrentHp() > 0 && lastTurnDefender.getCurrentHp() > 0) {
                duelResult = Result.DRAW;
            } else if (lastTurnAttacker.getName().equals(String.format("%s %s(%s)", heroDto.getName(), heroDto.getLastName(), userDto.getLogin()))) {
                duelResult = Result.ATTACKER;
            } else {
                duelResult = Result.DEFENDER;
            }
            assertEquals(duelDto.getResult(), duelResult.name());
        }
    }

    @Test
    @Description(value = "Тест на то, что после победы в сражении дается случайная добыча")
    void getLootTest() throws Exception {
        //given
        RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
        String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
        RequestParentTest.setAdminRights(mockMvc, token1);
        RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
        RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0,raceDto.getName())));
        RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
        RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Оружие", 50L)), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Меч", 2L, 1, "Оружие")), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Булава", 2L, 1, "Оружие")), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Копье", 2L, 1, "Оружие")), token1);
        //when
        int lootsize = 0;
        ResultActions resultActions = null;
        while (lootsize < 20) {
            resultActions = RequestParentTest.insertFight(mockMvc, "Слабак", token1);
            lootsize += objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), FightDto.class).getLoot().size();
        }
        resultActions
                //then
                .andExpect(
                        status().isCreated())
                .andExpect(
                        jsonPath("$.loot.[*].name", Matchers.anyOf(hasItem("Меч"), hasItem("Булава"), hasItem("Копье"))));
//                .andExpect(
//                        jsonPath("$.loot.[*].name", hasItem("Булава")))
//                .andExpect(
//                        jsonPath("$.loot.[*].name", hasItem("Копье")));
//                .andExpect(
//                        jsonPath("$.loot[*].name", hasItems("Меч", "Булава", "Копье")));
    }

    @Test
    @Description(value = "Тест на то, что после ничьей в сражении не дается добыча")
    void getLootDrawTest() throws Exception {
        //given
        RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
        String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
        RequestParentTest.setAdminRights(mockMvc, token1);
        RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
        RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1000, 1, 1000,raceDto.getName())));
        RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
        RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Оружие", 50L)), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Меч", 2L, 1, "Оружие")), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Булава", 2L, 1, "Оружие")), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Копье", 2L, 1, "Оружие")), token1);
        //when
        RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                //then
                .andExpect(
                        status().isCreated())
                .andExpect(
                        jsonPath("$.loot").value(empty())
                );
    }

    @Test
    @Description(value = "Тест на то, что после поражения в сражении не дается добыча")
    void getLootLoseTest() throws Exception {
        //given
        RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
        String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
        RequestParentTest.setAdminRights(mockMvc, token1);
        RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
        RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 100, 100, 100, 0,raceDto.getName())));
        RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
        RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Оружие", 50L)), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Меч", 2L, 1, "Оружие")), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Булава", 2L, 1, "Оружие")), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Копье", 2L, 1, "Оружие")), token1);
        //when
        RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                //then
                .andExpect(
                        status().isCreated())
                .andExpect(
                        jsonPath("$.loot").value(empty())
                );
    }

    @Test
    @Description(value = "Тест на то, что после победы в сражении дается случайная добыча размере в рамках навыка обыска")
    void getLootTest_InSearchCase() throws Exception {
        //given
        RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
        String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
        RequestParentTest.setAdminRights(mockMvc, token1);
        RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
        RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0,raceDto.getName())));
        RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
        RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Оружие", 50L)), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Меч", 2L, 1, "Оружие")), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Булава", 2L, 1, "Оружие")), token1);
        RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Копье", 2L, 1, "Оружие")), token1);
        //when
        int commonLootsize = 0;
        ResultActions resultActions = null;
        while (commonLootsize < 100) {
            resultActions = RequestParentTest.insertFight(mockMvc, "Слабак", token1);
            int currentLootSize = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), FightDto.class).getLoot().size();
            assertTrue(currentLootSize <= heroDto.getWis());
            commonLootsize += currentLootSize;
        }
    }
}
