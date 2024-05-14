package ru.gizka.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.model.race.RaceSize;

import java.util.Random;

import static org.hamcrest.Matchers.*;
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
    private RequestItemPatternDto itemPatternDto;
    private RequestProductDto productDto;

    @Autowired
    private FightControllerTest(MockMvc mockMvc,
                                ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() throws Exception {
        raceDto = new RequestRaceDto("Человек", true,
                0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

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
                .wis(10)
                .race("Человек")
                .build();

        creatureDto = RequestCreatureDto.builder()
                .name("Разбойник")
                .str(4)
                .dex(7)
                .con(5)
                .race(raceDto.getName())
                .build();

        productDto = new RequestProductDto(
                "Роскошь", 500);

        itemPatternDto = new RequestItemPatternDto(
                "Медаль", 1L, 1, productDto.getName());
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
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);

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
                            jsonPath("$[0].wis").value(heroDto.getWis()))
                    .andExpect(
                            jsonPath("$[0].createdAt").value(not(empty())))
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
                            jsonPath("$[0].currentHp").value(lessThanOrEqualTo(heroDto.getCon() * 3)))
                    .andExpect(
                            jsonPath("$[0].endurance").value(heroDto.getCon()))
                    .andExpect(
                            jsonPath("$[0].currentWeight").value(0))
                    .andExpect(
                            jsonPath("$[0].search").value(heroDto.getWis() + raceDto.getWisBonus()))
                    .andExpect(
                            jsonPath("$[0].treat").value(heroDto.getWis() + raceDto.getWisBonus()));

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
                            jsonPath("$.endurance").value(creatureDto.getCon()));
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

        @Test
        @Description(value = "Тест на симулирование сражения с перевесом")
        void Fight_simulate_WithOverweight() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Золотая гиря", heroDto.getStr() * 4000L, 1, productDto.getName())), token1);
            int lootsize = 0;
            while (lootsize == 0) {
                FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1).andReturn().getResponse().getContentAsString(), FightDto.class);
                lootsize = fightDto.getLoot().size();
            }

            requestBuilder = MockMvcRequestBuilders
                    .post(String.format("%s?name=%s", uri, creatureDto.getName()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Герой перегружен и не может сражаться")));
        }
    }

    @Nested
    @Description(value = "Тест на получение последнего сражения")
    class GetLatestTest {
        @Test
        @Description(value = "Тест на получение последнего сражения с проверкой, что сражение принадлежит именно данному герою")
        void GetLatest_success() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            creatureDto.setCon(1);
            creatureDto.setDex(1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            FightDto lastFight = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(new RequestAppUserDto("Boba", userDto.getPassword())));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(new RequestAppUserDto("Boba", userDto.getPassword())));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token2);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token2);
            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.id").value(lastFight.getId()))
                    .andExpect(
                            jsonPath("$.heroFighter.id").value(lastFight.getHeroFighter().getId()))
                    .andExpect(
                            jsonPath("$.creatureFighter.id").value(lastFight.getCreatureFighter().getId()))
                    .andExpect(
                            jsonPath("$.turns").value(not(empty())))
                    .andExpect(
                            jsonPath("$.result").value(lastFight.getResult()))
                    .andExpect(
                            jsonPath("$.createdAt").value(lastFight.getCreatedAt()));
        }

        @Test
        @Description(value = "Тест на получение последнего сражения, если новый герой, а сражение было у предыдущего")
        void GetLatest_IfNewHero() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Титан", 1000, 1000, 1000, raceDto.getName())));
            FightDto lastFight = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Титан", token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            Thread.sleep(20 * 1000);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У героя нет сражений"));
        }

        @Test
        @Description(value = "Тест на получение последнего сражения, если нового героя нет")
        void GetLatest_IfNoNewHero() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Титан", 1000, 1000, 1000, raceDto.getName())));
            FightDto lastFight = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Титан", token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.id").value(lastFight.getId()))
                    .andExpect(
                            jsonPath("$.heroFighter.id").value(lastFight.getHeroFighter().getId()))
                    .andExpect(
                            jsonPath("$.creatureFighter.id").value(lastFight.getCreatureFighter().getId()))
                    .andExpect(
                            jsonPath("$.turns").value(not(empty())))
                    .andExpect(
                            jsonPath("$.result").value(lastFight.getResult()))
                    .andExpect(
                            jsonPath("$.createdAt").value(lastFight.getCreatedAt()))
                    .andExpect(
                            jsonPath("$.loot").value(empty()));
        }

        @Test
        @Description(value = "Тест на получение последнего сражения, если вообще не было создано героев")
        void GetLatest_NoHeroes() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Титан", 1000, 1000, 1000, raceDto.getName())));
            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У пользователя нет героев"));
        }
    }
}
