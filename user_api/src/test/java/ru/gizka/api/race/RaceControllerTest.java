package ru.gizka.api.race;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.RequestParentTest;
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
public class RaceControllerTest {
    private String uri = "/api/race";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Random random;

    @Autowired
    private RaceControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @Nested
    @DisplayName(value = "Тесты на получение расы по названию")
    class GetByNameTest {
        private RequestAppUserDto userDto;
        private RequestRaceDto raceDto;
        private MockHttpServletRequestBuilder getRequest;

        @BeforeEach
        void setUp() {
            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            raceDto = new RequestRaceDto("Человек", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
        }

        @Test
        @Description(value = "Тест на получение расы")
        void Race_getByName_SuccessTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + raceDto.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.name").value(raceDto.getName()))
                    .andExpect(
                            jsonPath("$.isPlayable").value(raceDto.getIsPlayable()));
        }

        @Test
        @Description(value = "Тест на получение несуществующей расы")
        void Race_getByName_NotExist() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + "Эльф")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Раса не найдена:.*")));
        }

        @Test
        @Description(value = "Тест на получение расы null")
        void Race_getByName_NullName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + null)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Раса не найдена:.*")));
        }

        @Test
        @Description(value = "Тест на получение расы empty")
        void Race_getByName_EmptyName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + "")
                    .contentType(MediaType.APPLICATION_JSON)
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
        @Description(value = "Тест на получение расы blank")
        void Race_getByName_BlankName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + "     ")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Раса не найдена:.*")));
        }

        @Test
        @Description(value = "Тест на получение расы c длинным именем")
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
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }
    }

    @Nested
    @DisplayName(value = "Тесты на получение всех рас")
    class GetAllTest {
        private RequestAppUserDto userDto;
        private RequestRaceDto raceDto1;
        private RequestRaceDto raceDto2;
        private RequestRaceDto raceDto3;
        private RequestRaceDto raceDto4;
        private RequestRaceDto raceDto5;
        private MockHttpServletRequestBuilder getRequest;

        @BeforeEach
        void setUp() {
            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            raceDto1 = new RequestRaceDto("Человек", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

            raceDto2 = new RequestRaceDto("Эльф", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

            raceDto3 = new RequestRaceDto("Гном", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

            raceDto4 = new RequestRaceDto("Тролль", true,
                    0, 0, 0, 0, 0, RaceSize.GREAT.name());

            raceDto5 = new RequestRaceDto("Скелет", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
        }

        @Test
        @Description(value = "Тест на получение всех рас")
        void Race_getAll_SuccessTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto1));
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto2));
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto3));
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto4));
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto5));
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
        void Race_getAll_NoRacesTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/all")
                    .contentType(MediaType.APPLICATION_JSON)
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
