package ru.gizka.api.old;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.old.creature.RequestCreatureDto;
import ru.gizka.api.dto.old.fight.DuelDto;
import ru.gizka.api.dto.old.fight.FightDto;
import ru.gizka.api.dto.old.hero.RequestHeroDto;
import ru.gizka.api.dto.old.item.RequestItemPatternDto;
import ru.gizka.api.dto.old.item.RequestProductDto;
import ru.gizka.api.dto.old.race.RequestRaceDto;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.model.old.race.RaceSize;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class DuelControllerTest extends RequestParentTest {
    private String uri = "/api/user/hero/duel";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder requestBuilder;
    private String token;
    private RequestAppUserDto userDto;
    private RequestAppUserDto userDto2;
    private RequestHeroDto heroDto;
    private RequestHeroDto heroDto2;
    private RequestRaceDto raceDto;

    @Autowired
    private DuelControllerTest(MockMvc mockMvc,
                               ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() throws Exception {
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

    @Nested
    @DisplayName(value = "Тесты на проведение дуэли")
    class SimulateDuelTest {

        @Test
        @Description(value = "Тест на симулирование дуэли")
        void Duel_simulate_success() throws Exception {
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
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isCreated());
        }

        @Test
        @Description(value = "Тест на симулирование дуэли с защитой")
        void Duel_simulate_WithDef() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            raceDto.setDefBonus(10);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isCreated())
                    .andExpect(
                            jsonPath("$.result").value(containsString("DRAW")))
                    .andExpect(
                            jsonPath("$.heroFighters[0].currentHp").value(heroDto.getCon() * 3))
                    .andExpect(
                            jsonPath("$.heroFighters[1].currentHp").value(heroDto2.getCon() * 3));
        }

        @Test
        @Description(value = "Тест на дуэль, если нет героя")
        void Duel_noOwnHeroTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У одного из пользователей нет героя со статусом ALIVE."));
        }

        @Test
        @Description(value = "Тест на дуэль, если нет логина противника")
        void Duel_noOpponentLoginTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("Пользователь не найден"));
        }

        @Test
        @Description(value = "Тест на дуэль, если переданный логин null")
        void Duel_OpponentLoginNull() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, null))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("Пользователь не найден"));
        }

        @Test
        @Description(value = "Тест на дуэль, если переданный логин пустой")
        void Duel_OpponentLoginEmpty() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, ""))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на дуэль, если переданный логин состоит из пробелов")
        void Duel_OpponentLoginBlank() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, "      "))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest()
                    );
        }

        @Test
        @Description(value = "Тест на дуэль, если переданный логин состоит из более 255 символов")
        void Duel_OpponentLoginTooLong() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            Random random = new Random();
            StringBuilder login = new StringBuilder();
            for (int i = 0; i < 256; i++) {
                login.append(Character.toString('A' + random.nextInt(26)));
            }

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, login))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на дуэль против себя")
        void Duel_AttackerAsOpponent() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value("Нельзя выбрать своего героя в качестве соперника"));
        }

        @Test
        @Description(value = "Тест на дуэль, если у противника нет героя")
        void Duel_OpponentHasNoHero() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У одного из пользователей нет героя со статусом ALIVE."));
        }

        @Test
        @Description(value = "Тест на дуэль, что не изменила сущностей героев в базе данных")
        void Duel_NoChangesInDB() throws Exception {
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
            mockMvc.perform(requestBuilder);

            //when
            RequestParentTest.getCurrentHero(mockMvc, token)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].name").value(heroDto.getName()))
                    .andExpect(
                            jsonPath("$[0].lastname").value(heroDto.getLastName()))
                    .andExpect(
                            jsonPath("$[0].str").value(heroDto.getStr()))
                    .andExpect(
                            jsonPath("$[0].dex").value(heroDto.getDex()))
                    .andExpect(
                            jsonPath("$[0].con").value(heroDto.getCon()))
                    .andExpect(
                            jsonPath("$[0].wis").value(heroDto.getWis()))
                    .andExpect(
                            jsonPath("$[0].createdAt").value(Matchers.not(Matchers.empty())))
                    .andExpect(
                            jsonPath("$[0].userLogin").value(userDto.getLogin()))
                    .andExpect(
                            jsonPath("$[0].status").value("ALIVE"))
                    .andExpect(
                            jsonPath("$[0].race").value(heroDto.getRace()))
                    .andExpect(
                            jsonPath("$[0].minInit").value("0"))
                    .andExpect(
                            jsonPath("$[0].maxInit").value(heroDto.getDex()))
                    .andExpect(
                            jsonPath("$[0].minAttack").value("0"))
                    .andExpect(
                            jsonPath("$[0].maxAttack").value(heroDto.getDex()))
                    .andExpect(
                            jsonPath("$[0].minEvasion").value("0"))
                    .andExpect(
                            jsonPath("$[0].maxEvasion").value(heroDto.getDex()))
                    .andExpect(
                            jsonPath("$[0].minPhysDamage").value("0"))
                    .andExpect(
                            jsonPath("$[0].maxPhysDamage").value(heroDto.getStr()))
                    .andExpect(
                            jsonPath("$[0].maxHp").value(heroDto.getCon() * 3))
                    .andExpect(
                            jsonPath("$[0].currentHp").value(heroDto.getCon() * 3))
                    .andExpect(
                            jsonPath("$[0].endurance").value(heroDto.getCon()))
                    .andExpect(
                            jsonPath("$[0].currentWeight").value(0))
                    .andExpect(
                            jsonPath("$[0].search").value(heroDto.getWis() + raceDto.getWisBonus()))
                    .andExpect(
                            jsonPath("$[0].treat").value(heroDto.getWis() + raceDto.getWisBonus()));
            ;

            //when
            RequestParentTest.getCurrentHero(mockMvc, token2)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].name").value(heroDto2.getName()))
                    .andExpect(
                            jsonPath("$[0].lastname").value(heroDto2.getLastName()))
                    .andExpect(
                            jsonPath("$[0].str").value(heroDto2.getStr()))
                    .andExpect(
                            jsonPath("$[0].dex").value(heroDto2.getDex()))
                    .andExpect(
                            jsonPath("$[0].con").value(heroDto2.getCon()))
                    .andExpect(
                            jsonPath("$[0].wis").value(heroDto2.getWis()))
                    .andExpect(
                            jsonPath("$[0].createdAt").value(Matchers.not(Matchers.empty())))
                    .andExpect(
                            jsonPath("$[0].userLogin").value(userDto2.getLogin()))
                    .andExpect(
                            jsonPath("$[0].status").value("ALIVE"))
                    .andExpect(
                            jsonPath("$[0].race").value(heroDto2.getRace()))
                    .andExpect(
                            jsonPath("$[0].minInit").value("0"))
                    .andExpect(
                            jsonPath("$[0].maxInit").value(heroDto2.getDex()))
                    .andExpect(
                            jsonPath("$[0].minAttack").value("0"))
                    .andExpect(
                            jsonPath("$[0].maxAttack").value(heroDto2.getDex()))
                    .andExpect(
                            jsonPath("$[0].minEvasion").value("0"))
                    .andExpect(
                            jsonPath("$[0].maxEvasion").value(heroDto2.getDex()))
                    .andExpect(
                            jsonPath("$[0].minPhysDamage").value("0"))
                    .andExpect(
                            jsonPath("$[0].maxPhysDamage").value(heroDto2.getStr()))
                    .andExpect(
                            jsonPath("$[0].maxHp").value(heroDto2.getCon() * 3))
                    .andExpect(
                            jsonPath("$[0].currentHp").value(heroDto2.getCon() * 3))
                    .andExpect(
                            jsonPath("$[0].endurance").value(heroDto2.getCon()))
                    .andExpect(
                            jsonPath("$[0].currentWeight").value(0))
                    .andExpect(
                            jsonPath("$[0].search").value(heroDto.getWis() + raceDto.getWisBonus()))
                    .andExpect(
                            jsonPath("$[0].treat").value(heroDto.getWis() + raceDto.getWisBonus()));
        }

        @Test
        @Description(value = "Тест на симулирование дуэли, если у атакующего герой не ALIVE")
        void Duel_isNotAlive() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);
            RequestParentTest.insertCreature(mockMvc, token1,
                    objectMapper.writeValueAsString(new RequestCreatureDto(
                            "Титан",
                            1000,
                            1000,
                            1000,
                            0,
                            raceDto.getName())));

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            RequestParentTest.insertFight(mockMvc, "Титан", token);

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У одного из пользователей нет героя со статусом ALIVE."));
        }

        @Test
        @Description(value = "Тест на симулирование дуэли, если у защищающегося герой не ALIVE")
        void Duel_OpponentIsNotAlive() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);
            RequestParentTest.insertCreature(mockMvc, token1,
                    objectMapper.writeValueAsString(new RequestCreatureDto(
                            "Титан",
                            1000,
                            1000,
                            1000,
                            0,
                            raceDto.getName())));

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            RequestParentTest.insertFight(mockMvc, "Титан", token2);

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У одного из пользователей нет героя со статусом ALIVE."));
        }

        @Test
        @Description(value = "Тест на симулирование дуэли с перегрузом у атакующего")
        void Duel_simulate_AttackerOverweight() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Разбойник", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Роскошь", 1000L)), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Золотая гиря", heroDto.getStr() * 4000L, 1, "Роскошь")), token1);

            int lootSize = 0;
            while (lootSize == 0) {
                FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Разбойник", token1).andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Ваш герой перегружен и не может сражаться.")));
        }

        @Test
        @Description(value = "Тест на симулирование дуэли с перегрузом у защищающегося")
        void Duel_simulate_DefenderOverweight() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Разбойник", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Роскошь", 1000L)), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Золотая гиря", heroDto.getStr() * 4000L, 1, "Роскошь")), token1);
            RequestParentTest.insertFight(mockMvc, "Разбойник", token);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?login=%s", uri, userDto2.getLogin()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isCreated());
        }
    }

    @Nested
    @DisplayName(value = "Тесты на получение списка дуэлей для текущего героя")
    class GetAllDuelForCurrentHeroTest {

        @Test
        @Description(value = "Тест на получение дуэлей, если нет героя")
        void Duel_GetDuelsNoCurrentHero() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$", hasSize(0)));
        }

        @Test
        @Description(value = "Тест на получение дуэлей в сортированном виде")
        void Duel_GetSortedDuels() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);
            RequestParentTest.setAdminRights(mockMvc, token1);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertDuel(mockMvc, userDto2.getLogin(), token1);

            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token2);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertDuel(mockMvc, userDto2.getLogin(), token1);

            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token2);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            String response = mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$", hasSize(4)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<DuelDto> duels = objectMapper.readValue(response, new TypeReference<>() {
            });

            for (int i = 0; i < duels.size() - 1; i++) {
                Date createdAt1 = duels.get(i).getCreatedAt();
                Date createdAt2 = duels.get(i + 1).getCreatedAt();
                assertTrue(createdAt1.after(createdAt2) || createdAt1.equals(createdAt2));
            }
        }

        @Test
        @Description(value = "Тест на получение именно личных дуэлей в сортированном виде")
        void Duel_GetOwnSortedDuels() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(new RequestAppUserDto("Pupa", userDto.getPassword())));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token3 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(new RequestAppUserDto("Pupa", userDto.getPassword())));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(new RequestHeroDto("Йцукенг", "Йфя", 10, 10, 10, 10, raceDto.getName())), token3);
            RequestParentTest.setAdminRights(mockMvc, token1);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertDuel(mockMvc, userDto2.getLogin(), token1);

            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token2);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertDuel(mockMvc, userDto2.getLogin(), token1);

            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token2);

            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token3);

            token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            String response = mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$", hasSize(5)))
                    .andExpect(
                            jsonPath("$.[*].heroFighters.[*].name").value(hasItem(
                                    String.format("%s %s(%s)", heroDto.getName(), heroDto.getLastName(), userDto.getLogin()))))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<DuelDto> duels = objectMapper.readValue(response, new TypeReference<>() {
            });

            for (int i = 0; i < duels.size() - 1; i++) {
                Date createdAt1 = duels.get(i).getCreatedAt();
                Date createdAt2 = duels.get(i + 1).getCreatedAt();
                assertTrue(createdAt1.after(createdAt2) || createdAt1.equals(createdAt2));
            }
        }
    }
}
