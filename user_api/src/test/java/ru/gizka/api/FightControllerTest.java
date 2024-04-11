package ru.gizka.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.fight.DuelDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class FightControllerTest extends RequestParentTest {
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
    private FightControllerTest(MockMvc mockMvc,
                                ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() throws Exception {
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
                .race("Человек")
                .build();

        heroDto2 = RequestHeroDto.builder()
                .name("Lyakusha")
                .lastName("Swamp")
                .str(10)
                .dex(12)
                .con(8)
                .race("Человек")
                .build();
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
                            status().isCreated()
                    );
        }

        @Test
        @Description(value = "Тест на дуэль, если нет героя")
        void Duel_noOwnHeroTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
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
                            jsonPath("$[0].createdAt").value(Matchers.not(Matchers.empty())))
                    .andExpect(
                            jsonPath("$[0].userLogin").value(userDto.getLogin()))
                    .andExpect(
                            jsonPath("$[0].status").value("ALIVE"));

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
                            jsonPath("$[0].createdAt").value(Matchers.not(Matchers.empty())))
                    .andExpect(
                            jsonPath("$[0].userLogin").value(userDto2.getLogin()))
                    .andExpect(
                            jsonPath("$[0].status").value("ALIVE"));
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
    }
}
