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
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AdminRaceControllerTest {
    private String uri = "/api/admin/race";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder createRequest;
    private Random random;

    @Autowired
    private AdminRaceControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @Nested
    @DisplayName(value = "Тесты на создание новой расы администратором")
    class CreateTest {
        private RequestRaceDto raceDto;
        private RequestAppUserDto userDto;

        @BeforeEach
        void setUp() throws Exception {
            userDto = RequestAppUserDto.builder()
                    .login("Biba")
                    .password("Qwerty12345!")
                    .build();

            raceDto = RequestRaceDto.builder()
                    .name("Человек")
                    .isPlayable(true)
                    .build();

            createRequest = MockMvcRequestBuilders
                    .post(uri)
                    .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @Description(value = "Тест на создание расы")
        void Race_createSuccess() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isCreated())
                    .andExpect(
                            jsonPath("$.name").value(raceDto.getName()))
                    .andExpect(
                            jsonPath("$.isPlayable").value(raceDto.getIsPlayable()));
        }

        @Test
        @Description(value = "Тест на создание расы с одинаковым названием")
        void Race_create_sameName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(raceDto));
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание расы без названия")
        void Race_create_NoName() throws Exception {
            //given
            raceDto = RequestRaceDto.builder()
                    .isPlayable(true)
                    .build();
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание расы без названия")
        void Race_create_NullName() throws Exception {
            //given
            raceDto = RequestRaceDto.builder()
                    .name(null)
                    .isPlayable(true)
                    .build();
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание расы без названия")
        void Race_create_BlankName() throws Exception {
            //given
            raceDto = RequestRaceDto.builder()
                    .name("      ")
                    .isPlayable(true)
                    .build();
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание расы без названия")
        void Race_create_EmptyName() throws Exception {
            //given
            raceDto = RequestRaceDto.builder()
                    .name("")
                    .isPlayable(true)
                    .build();
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание расы без играбельности")
        void Race_create_NoIsPlayable() throws Exception {
            //given
            raceDto = RequestRaceDto.builder()
                    .name("Человек")
                    .build();
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание расы без играбельности")
        void Race_create_NullIsPlayable() throws Exception {
            //given
            raceDto = RequestRaceDto.builder()
                    .name("Человек")
                    .isPlayable(null)
                    .build();
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание расы без прав администратора")
        void Race_create_NoAdmin() throws Exception {
            //given
            raceDto = RequestRaceDto.builder()
                    .name("Человек")
                    .isPlayable(true)
                    .build();
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isForbidden());
        }

        @Test
        @Description(value = "Тест на создание расы c длинным именем")
        void Race_create_LongName() throws Exception {
            //given
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 101; i++) {
                name.append(Character.toString('А' + random.nextInt(33)));
            }
            raceDto = RequestRaceDto.builder()
                    .name(name.toString())
                    .isPlayable(true)
                    .build();

            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание расы c запрещенными символами")
        void Race_create_ForbiddenSymbols() throws Exception {
            //given
            raceDto = RequestRaceDto.builder()
                    .name("ЭльфZ")
                    .isPlayable(true)
                    .build();

            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsBytes(this.raceDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }
    }
}
