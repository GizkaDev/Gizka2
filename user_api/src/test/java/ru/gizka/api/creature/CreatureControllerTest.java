package ru.gizka.api.creature;

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
import ru.gizka.api.RequestParentTest;
import ru.gizka.api.dto.creature.RequestCreatureDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.appUser.RequestAppUserDto;
import ru.gizka.api.model.race.RaceSize;

import java.util.Random;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class CreatureControllerTest {
    private String uri = "/api/creature";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Random random;

    @Autowired
    private CreatureControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @Nested
    @DisplayName(value = "Тесты на получение моба по названию")
    class GetByNameTest {
        private RequestAppUserDto userDto;
        private RequestCreatureDto creatureDto;
        private RequestRaceDto raceDto;
        private MockHttpServletRequestBuilder getRequest;

        @BeforeEach
        void setUp() {
            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            raceDto = new RequestRaceDto("Монстр", true,
                    0, 0, 0, 0, 0, RaceSize.GIANT.name());

            creatureDto = new RequestCreatureDto(
                    "Злобоглаз",
                    4,
                    10,
                    7,
                    0,
                    raceDto.getName());
        }

        @Test
        @Description(value = "Тест на получение моба")
        void Creature_getByName_SuccessTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + creatureDto.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
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
                            jsonPath("$.endurance").value(creatureDto.getCon()))
                    .andExpect(
                            jsonPath("$.def").value(creatureDto.getDef() + raceDto.getDefBonus()));
        }

        @Test
        @Description(value = "Тест на получение несуществующего моба")
        void Race_getByName_NotExist() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + "Водяной элементаль")
                    .contentType(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Моб не найден:.*")));
        }

        @Test
        @Description(value = "Тест на получение моба null")
        void Race_getByName_NullName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + null)
                    .contentType(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Моб не найден:.*")));
        }

        @Test
        @Description(value = "Тест на получение моба empty")
        void Race_getByName_EmptyName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + "")
                    .contentType(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*No static resource.*")));
        }

        @Test
        @Description(value = "Тест на получение моба blank")
        void Race_getByName_BlankName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + "     ")
                    .contentType(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Моб не найден:.*")));
        }

        @Test
        @Description(value = "Тест на получение моба c длинным именем")
        void Race_getByName_LongName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 101; i++) {
                name.append(Character.toString('А' + random.nextInt(33)));
            }
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + name)
                    .contentType(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }
    }

    @Nested
    @DisplayName(value = "Тесты на получение всех мобов")
    class GetAllTest {
        private RequestAppUserDto userDto;
        private RequestRaceDto raceDto;
        private RequestCreatureDto creatureDto1;
        private RequestCreatureDto creatureDto2;
        private RequestCreatureDto creatureDto3;
        private RequestCreatureDto creatureDto4;
        private RequestCreatureDto creatureDto5;
        private MockHttpServletRequestBuilder getRequest;

        @BeforeEach
        void setUp() {
            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            raceDto = new RequestRaceDto("Дракон", false,
                    0, 0, 0, 0, 0, RaceSize.GIANT.name());

            creatureDto1 = new RequestCreatureDto(
                    "Черный дракон",
                    30,
                    20,
                    10,
                    0,
                    raceDto.getName());

            creatureDto2 = new RequestCreatureDto(
                    "Зеленый дракон",
                    20,
                    10,
                    30,
                    0,
                    raceDto.getName());

            creatureDto3 = new RequestCreatureDto(
                    "Красный дракон",
                    10,
                    30,
                    20,
                    0,
                    raceDto.getName());

            creatureDto4 = new RequestCreatureDto(
                    "Синий дракон",
                    15,
                    25,
                    20,
                    0,
                    raceDto.getName());

            creatureDto5 = new RequestCreatureDto(
                    "Ржавый дракон",
                    10,
                    25,
                    25,
                    0,
                    raceDto.getName());
        }

        @Test
        @Description(value = "Тест на получение всех мобов")
        void Creature_getAll_SuccessTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto1));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto2));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto3));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto4));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto5));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$", hasSize(5)));
        }

        @Test
        @Description(value = "Тест на получение всех рас, если нет рас")
        void Race_getAll_NoCreaturesTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/all")
                    .contentType(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }
}
