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

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AdminCreatureControllerTest {

    private String uri = "/api/admin/creature";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder createRequest;
    private Random random;

    @Autowired
    private AdminCreatureControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @Nested
    @DisplayName(value = "Тест на создание нового моба")
    class CreateTest {
        private RequestAppUserDto userDto;
        private RequestRaceDto raceDto;
        private RequestCreatureDto creatureDto;

        @BeforeEach
        void setUp() throws Exception {
            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            raceDto = new RequestRaceDto("Гоблин", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

            creatureDto = new RequestCreatureDto(
                    "Безумный гоблин",
                    4,
                    7,
                    5,
                    0,
                    raceDto.getName());

            createRequest = MockMvcRequestBuilders
                    .post(uri)
                    .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @Description(value = "Тест на создание моба")
        void Creature_createSuccess() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto));
            createRequest
                    .content(objectMapper.writeValueAsString(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isCreated())
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
                            jsonPath("$.def").value(raceDto.getDefBonus() + creatureDto.getDef()));
        }

        @Test
        @Description(value = "Тест на создание моба без прав администратора")
        void Creature_create_NoAdmin() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isForbidden());
        }

        @Test
        @Description(value = "Тест на создание моба c длинным именем")
        void Creature_create_LongName() throws Exception {
            //given
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 101; i++) {
                name.append(Character.toString('А' + random.nextInt(33)));
            }
            creatureDto.setName(name.toString());

            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название моба должно состоять из 1 - 100 символов.*")));
        }

        @Test
        @Description(value = "Тест на создание моба c названием 'null'")
        void Creature_create_nullName() throws Exception {
            //given
            creatureDto.setName("null");

            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Недопустимое название.*")));
        }

        @Test
        @Description(value = "Тест на создание моба без названия null")
        void Creature_create_NoName() throws Exception {
            //given
            creatureDto = new RequestCreatureDto(
                    null,
                    4,
                    7,
                    5,
                    0,
                    raceDto.getName());
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название моба должно состоять из 1 - 100 символов.*")));
        }

        @Test
        @Description(value = "Тест на создание моба без названия null")
        void Creature_create_NullName() throws Exception {
            //given
            creatureDto = new RequestCreatureDto(
                    null,
                    4,
                    7,
                    5,
                    0,
                    raceDto.getName());
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Недопустимое название.*")));
        }

        @Test
        @Description(value = "Тест на создание моба без названия blank")
        void Creature_create_BlankName() throws Exception {
            //given
            creatureDto = new RequestCreatureDto(
                    "      ",
                    4,
                    7,
                    5,
                    0,
                    raceDto.getName());
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название моба должно состоять из 1 - 100 символов.*")));
        }

        @Test
        @Description(value = "Тест на создание моба без названия empty")
        void Creature_create_EmptyName() throws Exception {
            //given
            creatureDto = new RequestCreatureDto(
                    "",
                    4,
                    7,
                    5,
                    0,
                    raceDto.getName());
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название моба должно состоять из 1 - 100 символов.*")));
        }

        @Test
        @Description(value = "Тест на создание моба c 0 характеристиками")
        void Creature_create_ZeroChar() throws Exception {
            //given
            creatureDto = new RequestCreatureDto(
                    "Безумный гоблин",
                    0,
                    0,
                    0,
                    0,
                    raceDto.getName());
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Сила должна быть больше 0.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Ловкость должна быть больше 0.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Выносливость должна быть больше 0.*")));
        }

        @Test
        @Description(value = "Тест на создание моба c null характеристиками")
        void Creature_create_NullChar() throws Exception {
            //given
            creatureDto = new RequestCreatureDto(
                    "Безумный гоблин",
                    null,
                    null,
                    null,
                    null,
                    raceDto.getName());
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Сила не должна быть пустой.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Ловкость не должна быть пустой.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Выносливость не должна быть пустой.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Защита должна быть 0 или больше.*")));
        }

        @Test
        @Description(value = "Тест на создание моба с одинаковым названием")
        void Race_create_sameName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto));
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Моб с таким названием уже существует.*")));
        }

        @Test
        @Description(value = "Тест на создание моба c отрицательной защитой")
        void Creature_create_NegativeDef() throws Exception {
            //given
            creatureDto = new RequestCreatureDto(
                    "Безумный гоблин",
                    0,
                    0,
                    0,
                    -1,
                    raceDto.getName());
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(creatureDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Защита должна быть 0 или больше.*")));
        }
    }
}
