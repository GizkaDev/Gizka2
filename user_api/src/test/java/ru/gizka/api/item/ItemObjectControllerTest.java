package ru.gizka.api.item;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.item.ResponseItemDto;
import ru.gizka.api.dto.item.armor.RequestArmorPatternDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.model.item.armor.ArmorType;
import ru.gizka.api.model.race.RaceSize;

import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ItemObjectControllerTest {
    private String uri = "/api/user/hero/inventory";
    private RequestAppUserDto userDto;
    private RequestHeroDto heroDto;
    private RequestRaceDto raceDto;
    private String token;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder requestBuilder;
    private Random random;

    @Autowired
    private ItemObjectControllerTest(MockMvc mockMvc,
                                     ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.random = new Random();
    }

    @BeforeEach
    void setUp() throws Exception {
        raceDto = new RequestRaceDto("Человек", true,
                0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

        heroDto = RequestHeroDto.builder()
                .name("NameЯ")
                .lastName("LastnameБ")
                .str(10)
                .dex(10)
                .con(10)
                .wis(10)
                .race(raceDto.getName())
                .build();

        userDto = RequestAppUserDto.builder()
                .login("Login123_.-")
                .password("Qwerty12345!")
                .build();

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
    @DisplayName(value = "Тесты на получение инвентаря текущего героя пользователя")
    class GetInventoryTest {
        private RequestAppUserDto userDto;
        private RequestHeroDto heroDto;
        private RequestRaceDto raceDto;
        private RequestCreatureDto creatureDto;
        private RequestItemPatternDto itemPatternDto;
        private RequestProductDto productDto;

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
                    .str(1)
                    .dex(1)
                    .con(1)
                    .race(raceDto.getName())
                    .build();

            productDto = new RequestProductDto(
                    "Роскошь", 500);

            itemPatternDto = new RequestItemPatternDto(
                    "Медаль", 1L, 1, productDto.getName());
        }

        @Test
        @Description(value = "Тест на получение инвентаря текущего героя пользователя")
        void Hero_getWithInventory_success() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$").value(hasSize(fightDto.getLoot().size())));
        }

        @Test
        @Description(value = "Тест на получение инвентаря с проверкой на принадлежность")
        void Hero_getWithInventory_CheckOwner() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);

            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(new RequestAppUserDto("Boba", userDto.getPassword())));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(new RequestAppUserDto("Boba", userDto.getPassword())));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(new RequestHeroDto("Герой", "Герой", 10, 10, 10, 10, raceDto.getName())), token2);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Золотой слиток", 1000L, 5, productDto.getName())), token1);
            FightDto fightDto2 = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token2)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$").value(hasSize(fightDto.getLoot().size())));

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token2))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$").value(hasSize(fightDto2.getLoot().size())));
        }

        @Test
        @Description(value = "Тест на получение инвентаря, если текущего героя нет")
        void Hero_getWithInventory_NoHero() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("У пользователя нет героя со статусом ALIVE.")));
        }

        @Test
        @Description(value = "Тест на получение инвентаря, если текущего герой мертв")
        void Hero_getWithInventory_DeadHero() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Титан", 100, 100, 100, raceDto.getName())));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, "Титан", token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("У пользователя нет героя со статусом ALIVE.")));
        }

        @Test
        @Description(value = "Тест на получение инвентаря, что предметы скапливаются")
        void Hero_getWithInventory_AddItems() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            FightDto fightDto1 = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            FightDto fightDto2 = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            FightDto fightDto3 = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            FightDto fightDto4 = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$").value(hasSize(fightDto1.getLoot().size() + fightDto2.getLoot().size() +
                                    fightDto3.getLoot().size() + fightDto4.getLoot().size())));
        }

        @Test
        @Description(value = "Тест на получение инвентаря текущего героя пользователя, если инвентарь пуст")
        void Hero_getWithInventory_EmptyInventory() throws Exception {
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
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$").value(hasSize(0)));
        }

        @Test
        @Description(value = "Тест на получение инвентаря текущего героя пользователя, если выпадают не только доспехи")
        void Hero_getWithInventory_NotOnly() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            int lootSize = 0;
            boolean onlySame = true;
            FightDto fightDto = null;
            while (lootSize == 0 && onlySame) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
                if (lootSize != 0) {
                    String nameSample = fightDto.getLoot().get(0).getName();
                    for (ResponseItemDto itemDto : fightDto.getLoot()) {
                        if (nameSample.equals(itemDto.getName())) {
                            onlySame = false;
                            break;
                        }
                    }
                }
            }

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$").value(hasSize(fightDto.getLoot().size())));
        }

        @Test
        @Description(value = "Тест на получение инвентаря текущего героя пользователя, если выпадают доспехи")
        void Hero_getWithInventory_OnlyArmor() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Доспехи", 10000)), token1);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(new RequestArmorPatternDto("Кольчуга", 10000L, 2, 4, -2, ArmorType.MEDIUM.toString())), token1);
            int lootSize = 0;
            FightDto fightDto = null;
            while (lootSize == 0) {
                fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                        .andReturn().getResponse().getContentAsString(), FightDto.class);
                lootSize = fightDto.getLoot().size();
            }

            requestBuilder = MockMvcRequestBuilders
                    .get(uri)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$").value(hasSize(fightDto.getLoot().size())));
        }
    }

    @Nested
    @DisplayName(value = "Тесты на сброс вещей из инвентаря")
    class DropTest {
        private RequestAppUserDto userDto;
        private RequestHeroDto heroDto;
        private RequestRaceDto raceDto;
        private RequestCreatureDto creatureDto;
        private RequestItemPatternDto itemPatternDto;
        private RequestProductDto productDto;

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
                    .str(1)
                    .dex(1)
                    .con(1)
                    .race(raceDto.getName())
                    .build();

            productDto = new RequestProductDto(
                    "Роскошь", 500);

            itemPatternDto = new RequestItemPatternDto(
                    "Медаль", 100L, 1, productDto.getName());
        }

        @Test
        @Description(value = "Тест на сброс предмета из инвентаря текущего героя пользователя")
        void ItemObject_Drop_success() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            int lootsize = 0;
            while (lootsize == 0) {
                FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1).andReturn().getResponse().getContentAsString(), FightDto.class);
                lootsize = fightDto.getLoot().size();
            }
            ResponseItemDto[] beforeDrop = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .delete(uri + "/" + itemPatternDto.getName())
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNoContent());

            ResponseItemDto[] afterDrop = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            assertEquals(beforeDrop.length, afterDrop.length + 1);
        }

        @Test
        @Description(value = "Тест на сброс предмета из инвентаря текущего героя пользователя, если нет такого предмета")
        void ItemObject_Drop_NoItem() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            ResponseItemDto[] beforeDrop = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .delete(uri + "/" + "Другой предмет")
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("не найден в инвентаре")));
        }

        @Test
        @Description(value = "Тест на сброс предмета из инвентаря текущего героя пользователя, если нет живого героя")
        void ItemObject_Drop_NoAliveHero() throws Exception {
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
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(new RequestCreatureDto("Титан", 100, 100, 100, raceDto.getName())));
            RequestParentTest.insertFight(mockMvc, "Титан", token1);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .delete(uri + "/" + itemPatternDto.getName())
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

        @Test
        @Description(value = "Тест на сброс предмета из инвентаря текущего героя пользователя, если нет такого предмета")
        void ItemObject_Drop_NullItem() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            ResponseItemDto[] beforeDrop = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .delete(uri + "/" + null)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("не найден в инвентаре")));
        }

        @Test
        @Description(value = "Тест на сброс предмета из инвентаря текущего героя пользователя, если нет такого предмета")
        void ItemObject_Drop_EmptyItem() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            ResponseItemDto[] beforeDrop = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .delete(uri + "/" + "")
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("No static resource")));
        }

        @Test
        @Description(value = "Тест на сброс предмета из инвентаря текущего героя пользователя, если нет такого предмета")
        void ItemObject_Drop_BlankItem() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token1)
                    .andReturn().getResponse().getContentAsString(), FightDto.class);
            ResponseItemDto[] beforeDrop = objectMapper.readValue(RequestParentTest.getInventory(mockMvc, token1)
                    .andReturn().getResponse().getContentAsString(), ResponseItemDto[].class);

            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 201; i++) {
                name.append(Character.toString('А' + random.nextInt(33)));
            }
            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .delete(uri + "/" + name)
                    .header("Authorization", String.format("Bearer %s", token1))
                    .contentType(MediaType.APPLICATION_JSON);

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }
    }
}
