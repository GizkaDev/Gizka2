package ru.gizka.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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
import ru.gizka.api.dto.creature.RequestCreatureDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class FightControllerTest {
    private String uri = "/api/user/hero/fight";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder requestBuilder;
    private String token;
    private RequestAppUserDto userDto;
    private RequestHeroDto heroDto;
    private RequestRaceDto raceDto;
    private RequestCreatureDto creatureDto;

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

        heroDto = RequestHeroDto.builder()
                .name("Gizka")
                .lastName("Green")
                .str(10)
                .dex(8)
                .con(12)
                .race("Человек")
                .build();

        creatureDto = RequestCreatureDto.builder()
                .name("Разбойник")
                .str(4)
                .dex(7)
                .con(5)
                .race(raceDto.getName())
                .build();
    }

    @Nested
    @Description(value = "Тест на проведение сражений")
    class SumilateFightTest {

        @Test
        @Description(value = "Тест на симулирование сражения")
        void Fight_simulate_success() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));


            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, creatureDto.getName()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isCreated());
        }

        @Test
        @Description(value = "Тест на сражение, если нет героя")
        void Fight_noOwnHeroTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, creatureDto.getName()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У пользователя нет героя со статусом ALIVE."));
        }

        @Test
        @Description(value = "Тест на сражение, если нет такого моба")
        void Fight_noCreatureTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, creatureDto.getName()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("Моб с таким названием не найден"));
        }

        @Test
        @Description(value = "Тест на симулирование сражения, если название моба null")
        void Fight_creatureNameNull() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));


            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, null))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("Моб с таким названием не найден"));
        }

        @Test
        @Description(value = "Тест на симулирование сражения, если название моба пустое")
        void Fight_creatureNameEmpty() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));


            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, ""))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на симулирование сражения, если название моба из пробелов")
        void Fight_creatureNameBlank() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));


            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, "       "))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на сражение, если переданное название моба состоит из более 255 символов")
        void Fight_CreatureNameTooLong() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);

            Random random = new Random();
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 256; i++) {
                name.append(Character.toString('A' + random.nextInt(26)));
            }

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, name))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на сражение, что не изменила сущностей героя в базе данных, кроме хп, и сущности моба")
        void Fight_OnlyHpChangesInDB() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, creatureDto.getName()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            mockMvc.perform(requestBuilder);

            //when
            RequestParentTest.getCurrentHero(mockMvc, token1)
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
                            jsonPath("$[0].currentHp").value(Matchers.lessThanOrEqualTo(heroDto.getCon() * 3)))
                    .andExpect(
                            jsonPath("$[0].currentCon").value(heroDto.getCon()));

            //when
            RequestParentTest.getCreature(mockMvc, token1, creatureDto.getName())
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.name").value(creatureDto.getName()))
                    .andExpect(
                            jsonPath("$.str").value(creatureDto.getStr()))
                    .andExpect(
                            jsonPath("$.dex").value(creatureDto.getDex()))
                    .andExpect(
                            jsonPath("$.con").value(creatureDto.getCon()))
                    .andExpect(
                            jsonPath("$.race").value(creatureDto.getRace()))
                    .andExpect(
                            jsonPath("$.minInit").value("0"))
                    .andExpect(
                            jsonPath("$.maxInit").value(creatureDto.getDex()))
                    .andExpect(
                            jsonPath("$.minAttack").value("0"))
                    .andExpect(
                            jsonPath("$.maxAttack").value(creatureDto.getDex()))
                    .andExpect(
                            jsonPath("$.minEvasion").value("0"))
                    .andExpect(
                            jsonPath("$.maxEvasion").value(creatureDto.getDex()))
                    .andExpect(
                            jsonPath("$.minPhysDamage").value("0"))
                    .andExpect(
                            jsonPath("$.maxPhysDamage").value(creatureDto.getStr()))
                    .andExpect(
                            jsonPath("$.maxHp").value(creatureDto.getCon() * 3))
                    .andExpect(
                            jsonPath("$.currentHp").value(creatureDto.getCon() * 3))
                    .andExpect(
                            jsonPath("$.currentCon").value(creatureDto.getCon()));
        }

        @Test
        @Description(value = "Тест на сражение, что герой может погибнуть и стать неиграбельным")
        void Fight_deathTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestCreatureDto megaCreature = RequestCreatureDto.builder()
                    .name("Титан")
                    .str(1000)
                    .dex(1000)
                    .con(1000)
                    .race(raceDto.getName())
                    .build();
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(megaCreature));

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, megaCreature.getName()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            mockMvc.perform(requestBuilder);
            //when
            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, creatureDto.getName()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У пользователя нет героя со статусом ALIVE."));
        }
    }
}
