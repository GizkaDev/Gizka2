package ru.gizka.api.item.armor;

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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.RequestParentTest;
import ru.gizka.api.dto.creature.RequestCreatureDto;
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.dto.fight.Turn;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.hero.ResponseHeroDto;
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.dto.item.armor.RequestArmorPatternDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.model.item.armor.ArmorType;
import ru.gizka.api.model.race.RaceSize;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ArmorObjectControllerTest {
    private String uri = "/api/user/hero/inventory/armor";
    private RequestAppUserDto userDto;
    private RequestHeroDto heroDto;
    private RequestRaceDto raceDto;
    private String token;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder requestBuilder;
    private Random random;

    @Autowired
    private ArmorObjectControllerTest(MockMvc mockMvc,
                                      ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.random = new Random();
    }

    @BeforeEach
    void setUp() throws Exception {
        raceDto = new RequestRaceDto("Человек", true,
                0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

        heroDto = new RequestHeroDto(
                "NameЯ",
                "LastnameБ",
                10,
                10,
                10,
                10,
                raceDto.getName());

        userDto = new RequestAppUserDto(
                "Biba",
                "Qwerty12345!");

        RequestBuilder userCreationBuilder =
                MockMvcRequestBuilders
                        .post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto));
        mockMvc.perform(userCreationBuilder);

        RequestBuilder tokenRequestBuilder = MockMvcRequestBuilders.post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDto));

        token = mockMvc.perform(tokenRequestBuilder)
                .andReturn()
                .getResponse()
                .getContentAsString();

        requestBuilder = MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Nested
    @DisplayName(value = "Тесты на надевание брони")
    class EquipTest {
        private RequestAppUserDto userDto;
        private RequestHeroDto heroDto;
        private RequestRaceDto raceDto;
        private RequestCreatureDto creatureDto;
        private RequestItemPatternDto itemPatternDto;
        private RequestProductDto productDto;

        @BeforeEach
        void setUp() throws Exception {
            raceDto = new RequestRaceDto("Человек", true,
                    0, 0, 0, 0, 1, RaceSize.AVERAGE.name());

            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            heroDto = new RequestHeroDto(
                    "Gizka",
                    "Green",
                    10,
                    8,
                    12,
                    10,
                    "Человек");


            creatureDto = new RequestCreatureDto(
                    "Разбойник",
                    10,
                    10,
                    10,
                    2,
                    raceDto.getName());
        }

        @Test
        @Description(value = "Тест на надевание брони текущего героя пользователя, если выпадают доспехи")
        void ArmorObject_equipArmor_success() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            ResponseHeroDto[] beforeEquipHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);

            requestBuilder = MockMvcRequestBuilders
                    .put(uri + "/" + beforeEquip[0].getId())
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.equippedArmor.id").value(beforeEquip[0].getId()))
                    .andExpect(
                            jsonPath("$.currentWeight").value(beforeEquipHero[0].getCurrentWeight()))
                    .andExpect(
                            jsonPath("$.def").value(beforeEquipHero[0].getDef()));

            ResponseHeroDto[] afterEquipHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);
            assertEquals(afterEquipHero[0].getEquippedArmor().getId(), beforeEquip[0].getId());
        }

        @Test
        @Description(value = "Тест на надевание брони текущего героя пользователя, если это не доспех")
        void ArmorObject_equipArmor_NotArmor() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Роскошь", 10000L)), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Медаль", 1L, 2, "Роскошь")), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            requestBuilder = MockMvcRequestBuilders
                    .put(uri + "/" + beforeEquip[0].getId())
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("Предмет не найден"));
        }


        @Test
        @Description(value = "Тест на симулирование сражения с защитой после надевания доспеха")
        void ArmorObject_simulate_WithArmor() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }
            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            RequestParentTest.equipArmor(mockMvc, beforeEquip[0].getId().toString(), token1);

            //when
            FightDto fightDto2 = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    //then
                    .andExpect(
                            status().isCreated())
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            for (Turn turn : fightDto2.getTurns()) {
                assertTrue(turn.getDef() == raceDto.getDefBonus() + 4 || turn.getDef() == raceDto.getDefBonus() + creatureDto.getDef());
            }
        }

        @Test
        @Description(value = "Тест на надевание брони текущего героя пользователя, если нет доспеха в инвентаре")
        void ArmorObject_equipArmor_NoArmor() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(new RequestAppUserDto("Boba", "Qwerty12345!")));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(new RequestAppUserDto("Boba", "Qwerty12345!")));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token2);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token2)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token2)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            ResponseHeroDto[] beforeEquipHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);

            requestBuilder = MockMvcRequestBuilders
                    .put(uri + "/" + beforeEquip[0].getId())
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value("Такого предмета нет в инвентаре"));
        }

        @Test
        @Description(value = "Тест на надевание брони текущего героя пользователя, предмет должен пропасть из инвентаря")
        void ArmorObject_equipArmor_removeFromInventory() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            RequestParentTest.equipArmor(mockMvc, beforeEquip[0].getId().toString(), token1);

            ResponseItemDto[] afterEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            assertEquals(afterEquip.length + 1, beforeEquip.length);
        }

        @Test
        @Description(value = "Тест на надевание брони текущего героя пользователя, если выбросить предмет")
        void ArmorObject_equipArmor_dropArmor() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            RequestParentTest.equipArmor(mockMvc, beforeEquip[0].getId().toString(), token1);

            ResponseItemDto[] afterEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            RequestParentTest.dropItem(mockMvc, beforeEquip[0].getId().toString(), token1)
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("Предмет не найден в инвентаре"));
        }

        @Test
        @Description(value = "Тест на надевание брони текущего героя пользователя, если уже были надеты доспехи")
        void ArmorObject_equipArmor_ChangeArmor() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 1L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize < 2) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            ResponseHeroDto[] beforeEquipHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);

            RequestParentTest.equipArmor(mockMvc, beforeEquip[0].getId().toString(), token1);
            //when
            RequestParentTest.equipArmor(mockMvc, beforeEquip[1].getId().toString(), token1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.equippedArmor.id").value(beforeEquip[1].getId()))
                    .andExpect(
                            jsonPath("$.currentWeight").value(beforeEquipHero[0].getCurrentWeight()))
                    .andExpect(
                            jsonPath("$.def").value(beforeEquipHero[0].getDef()));

            ResponseItemDto[] afterEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);
            assertEquals(afterEquip.length, beforeEquip.length - 1);
            assertTrue(List.of(afterEquip).contains(beforeEquip[0]));
        }

        @Test
        @Description(value = "Тест на надевание брони текущего героя пользователя, если нет героя")
        void ArmorObject_equipArmor_NoHero() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Титан", 1000, 1000, 1000, 0, raceDto.getName())));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 1L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Титан", token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);

            requestBuilder = MockMvcRequestBuilders
                    .put(uri + "/" + beforeEquip[0].getId())
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("У пользователя нет героя со статусом ALIVE")));
        }
    }

    @Nested
    @DisplayName(value = "Тесты на снятие брони")
    class TakeOffTest {
        private RequestAppUserDto userDto;
        private RequestHeroDto heroDto;
        private RequestRaceDto raceDto;
        private RequestCreatureDto creatureDto;
        private RequestItemPatternDto itemPatternDto;
        private RequestProductDto productDto;

        @BeforeEach
        void setUp() throws Exception {
            raceDto = new RequestRaceDto("Человек", true,
                    0, 0, 0, 0, 1, RaceSize.AVERAGE.name());

            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            heroDto = new RequestHeroDto(
                    "Gizka",
                    "Green",
                    10,
                    8,
                    12,
                    10,
                    "Человек");


            creatureDto = new RequestCreatureDto(
                    "Разбойник",
                    10,
                    10,
                    10,
                    2,
                    raceDto.getName());
        }

        @Test
        @Description(value = "Тест на снятие брони текущего героя пользователя, если выпадают доспехи")
        void ArmorObject_takeOffArmor_success() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            ResponseHeroDto[] beforeEquipHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);

            RequestParentTest.equipArmor(mockMvc, beforeEquip[0].getId().toString(), token1);

            requestBuilder = MockMvcRequestBuilders
                    .delete(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.equippedArmor.id").value(Matchers.nullValue()))
                    .andExpect(
                            jsonPath("$.currentWeight").value(beforeEquipHero[0].getCurrentWeight()))
                    .andExpect(
                            jsonPath("$.def").value(beforeEquipHero[0].getDef()));

            ResponseHeroDto[] afterTakeOffHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);
            assertEquals(afterTakeOffHero[0].getCurrentWeight(), beforeEquipHero[0].getCurrentWeight());
        }

        @Test
        @Description(value = "Тест на снятие брони текущего героя пользователя, если не был надет доспех")
        void ArmorObject_equipArmor_NoEquipped() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            ResponseHeroDto[] beforeEquipHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);

            requestBuilder = MockMvcRequestBuilders
                    .delete(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("У героя не экипирован доспех")));

            ResponseHeroDto[] afterTakeOffHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);
            assertEquals(afterTakeOffHero[0].getCurrentWeight(), beforeEquipHero[0].getCurrentWeight());
        }

        @Test
        @Description(value = "Тест на снятие брони текущего героя пользователя, если нет героя")
        void ArmorObject_takeOffArmor_NoHero() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Титан", 1000, 1000, 1000, 0, raceDto.getName())));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Слабак", 1, 1, 1, 0, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000L)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Слабак", token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            ResponseItemDto[] beforeEquip = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            ResponseHeroDto[] beforeEquipHero = objectMapper.readValue(RequestParentTest.getCurrentHero(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto[].class);

            RequestParentTest.equipArmor(mockMvc, beforeEquip[0].getId().toString(), token1);

            fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Титан", token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);

            //when
            RequestParentTest.takeOffArmor(mockMvc, token1)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("У пользователя нет героя со статусом ALIVE.")));
        }
    }
}
