package ru.gizka.api.fight;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.RequestParentTest;
import ru.gizka.api.dto.fight.DuelDto;
import ru.gizka.api.dto.fight.Fighter;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.model.fight.Result;
import ru.gizka.api.service.fightLogic.FightLogic;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class FightLogicTest extends RequestParentTest {
    private FightLogic fightLogic;
    private Fighter fighter1;
    private Fighter fighter2;
    private RequestAppUserDto userDto;
    private RequestAppUserDto userDto2;
    private RequestHeroDto heroDto;
    private RequestHeroDto heroDto2;
    private MockHttpServletRequestBuilder requestBuilder;
    private String uri = "/api/user/hero/duel";
    private String token;
    private RequestRaceDto raceDto;;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    private FightLogicTest(MockMvc mockMvc,
                           ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        fightLogic = new FightLogic(null, null, null,
                null, null, null);

        fighter1 = new Fighter();
        fighter1.setCon(15);

        fighter2 = new Fighter();
        fighter2.setCon(9);

        raceDto = RequestRaceDto.builder()
                .name("Человек")
                .isPlayable(true)
                .build();

        userDto = RequestAppUserDto.builder()
                .login("Biba")
                .password("Qwerty12345!")
                .build();

        userDto2 = RequestAppUserDto.builder()
                .login("Boba")
                .password("Qwerty12345!")
                .build();

        heroDto = RequestHeroDto.builder()
                .name("Gizka")
                .lastName("Green")
                .str(10)
                .dex(8)
                .con(12)
                .race(raceDto.getName())
                .build();

        heroDto2 = RequestHeroDto.builder()
                .name("Lyakusha")
                .lastName("Swamp")
                .str(10)
                .dex(12)
                .con(8)
                .race(raceDto.getName())
                .build();
    }

    @Test
    @Description(value = "Тест на определение количества раундов")
    void testGetMaxTurns() throws Exception {
        //given
        Class<?> clazz = fightLogic.getClass();
        Method method = clazz.getDeclaredMethod("getMaxTurns", Fighter.class, Fighter.class);
        method.setAccessible(true);
        //when
        Integer result = (int) method.invoke(fightLogic, fighter1, fighter2);
        //then
        assertEquals(Math.max(fighter1.getCon(), fighter2.getCon()), result);
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
            } else if (lastTurnAttacker.getUserLogin().equals(userDto.getLogin())) {
                duelResult = Result.ATTACKER;
            } else {
                duelResult = Result.DEFENDER;
            }
            assertEquals(duelDto.getResult(), duelResult.name());
        }
    }
}
