package ru.gizka.api;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.creature.RequestCreatureDto;
import ru.gizka.api.dto.fight.FightDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.item.RequestItemPatternDto;
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.notification.NotificationDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.model.race.RaceSize;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class NotificationControllerTest extends RequestParentTest {
    private String uri = "/api/user/notification";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private RequestAppUserDto userDto;
    private RequestAppUserDto userDto2;
    private RequestHeroDto heroDto;
    private RequestHeroDto heroDto2;
    private RequestRaceDto raceDto;
    private RequestCreatureDto weakCreatureDto;
    private RequestCreatureDto strongCreatureDto;
    private RequestItemPatternDto itemPatternDto;
    private RequestProductDto productDto;

    @Autowired
    private NotificationControllerTest(MockMvc mockMvc,
                                       ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        raceDto = new RequestRaceDto("Человек", true,
                0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

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
                .wis(10)
                .race("Человек")
                .build();

        heroDto2 = RequestHeroDto.builder()
                .name("Lyakusha")
                .lastName("Swamp")
                .str(10)
                .dex(12)
                .con(8)
                .wis(10)
                .race("Человек")
                .build();

        weakCreatureDto = RequestCreatureDto.builder()
                .name("Разбойник")
                .str(5)
                .dex(5)
                .con(5)
                .race(raceDto.getName())
                .build();

        strongCreatureDto = RequestCreatureDto.builder()
                .name("Примарх")
                .str(1000)
                .dex(1000)
                .con(1000)
                .race(raceDto.getName())
                .build();

        productDto = new RequestProductDto(
                "Роскошь", 500);

        itemPatternDto = new RequestItemPatternDto(
                "Медаль", 1L, 1, productDto.getName());
    }

    @Nested
    @DisplayName(value = "Тесты на получение оповещений")
    class GetAllSortedByDateTest {

        @Test
        @Description(value = "Тест на получение оповещений в сортированном виде")
        void Notification_GetAllSortedByDate_Success() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertDuel(mockMvc, userDto2.getLogin(), token1);

            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token2);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertDuel(mockMvc, userDto2.getLogin(), token1);

            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token2);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));

            MockHttpServletRequestBuilder eventRequest2 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token2));

            //when
            String response1 = mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String response2 = mockMvc.perform(eventRequest2)
                    //then
                    .andExpect(
                            status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<NotificationDto> events1 = objectMapper.readValue(response1, new TypeReference<>() {
            });
            List<NotificationDto> events2 = objectMapper.readValue(response2, new TypeReference<>() {
            });

            for (int i = 0; i < events1.size() - 1; i++) {
                Date createdAt1E1 = events1.get(i).getCreatedAt();
                Date createdAt2E1 = events1.get(i + 1).getCreatedAt();
                assertTrue(createdAt1E1.after(createdAt2E1) || createdAt1E1.equals(createdAt2E1));

                Date createdAt1E2 = events2.get(i).getCreatedAt();
                Date createdAt2E2 = events2.get(i + 1).getCreatedAt();
                assertTrue(createdAt1E2.after(createdAt2E1) || createdAt1E2.equals(createdAt2E2));
            }
        }

        @Test
        @Description(value = "Тест на получение оповещений о сражении при победе")
        void Notification_WinFightNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(weakCreatureDto));
            RequestParentTest.insertFight(mockMvc, weakCreatureDto.getName(), token1);
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value("Вы встретились в бою с Разбойник и победили."));
        }

        @Test
        @Description(value = "Тест на получение оповещений о сражении при поражении")
        void Notification_LostFightNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(strongCreatureDto));
            RequestParentTest.insertFight(mockMvc, strongCreatureDto.getName(), token1);
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$..message", hasItems("Ваш герой Gizka Green погиб", "Вы встретились в бою с Примарх и проиграли.")));
        }

        @Test
        @Description(value = "Тест на получение оповещений о дуэли атакующего")
        void Notification_AttackerDuelNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);
            RequestParentTest.insertDuel(mockMvc,userDto2.getLogin(), token1);
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value(containsString("Вы вызвали на дуэль Lyakusha Swamp(Boba)")));
        }

        @Test
        @Description(value = "Тест на получение оповещений о дуэли защищающегося")
        void Notification_DefenderDuelNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);
            RequestParentTest.insertDuel(mockMvc,userDto2.getLogin(), token1);
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token2));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value(containsString("Вас вызвал на дуэль Gizka Green(Biba)")));
        }

        @Test
        @Description(value = "Тест на получение оповещений о создании героя")
        void Notification_NewHeroNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value("Еще один авантюрист Gizka Green готов к приключениям"));
        }

        @Test
        @Description(value = "Тест на получение оповещений о лечении ран")
        void Notification_TreatNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            weakCreatureDto.setDex(100);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(weakCreatureDto));
            RequestParentTest.insertFight(mockMvc, weakCreatureDto.getName(), token1);
            RequestParentTest.treat(mockMvc, token1);
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value(containsString("Герой Gizka Green перевязал свои раны на")));
        }

        @Test
        @Description(value = "Тест на получение оповещений о получении добычи")
        void Notification_LootNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            weakCreatureDto.setDex(1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(weakCreatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(itemPatternDto), token1);
            int lootsize = 0;
            while (lootsize == 0) {
                FightDto fightDto = objectMapper.readValue(RequestParentTest.insertFight(mockMvc, weakCreatureDto.getName(), token1).andReturn().getResponse().getContentAsString(), FightDto.class);
                lootsize = fightDto.getLoot().size();
            }
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value(containsString("С поверженного врага вы сняли")));
        }

        @Test
        @Description(value = "Тест на нет оповещений о получении добычи при ничьей")
        void Notification_DrawLootNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            weakCreatureDto.setDex(100);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(weakCreatureDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(new RequestProductDto("Броня", 30)), token1);
            RequestParentTest.insertItemPattern(mockMvc, objectMapper.writeValueAsString(new RequestItemPatternDto("Кольчуга", 5L, 30, "Броня")), token1);
            RequestParentTest.insertFight(mockMvc, weakCreatureDto.getName(), token1);
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value(containsString("Вы встретились в бою с Разбойник , и у вас ничья.")));
        }

        @Test
        @Description(value = "Тест на нет оповещений о получении добычи при поражении")
        void Notification_LostLootNotification() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(strongCreatureDto));
            RequestParentTest.insertFight(mockMvc, strongCreatureDto.getName(), token1);
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value(containsString("Ваш герой Gizka Green погиб")));
        }

        @Test
        @Description(value = "Тест на оповещения, что принадлежат определенному пользователю")
        void Notification_CheckOwner() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertCreature(mockMvc, token1, objectMapper.writeValueAsString(strongCreatureDto));
            RequestParentTest.insertFight(mockMvc, strongCreatureDto.getName(), token1);

            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token2);
            RequestParentTest.insertFight(mockMvc, weakCreatureDto.getName(), token2);

            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));
            //when
            mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].message").value(containsString("Ваш герой Gizka Green погиб")));
        }
    }
}
