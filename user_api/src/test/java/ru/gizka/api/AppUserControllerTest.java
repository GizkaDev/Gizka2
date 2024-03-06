package ru.gizka.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.RequestAppUserDto;

import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AppUserControllerTest {
    private String uri = "/api/user";
    @Value("${jwt.suitability.minutes}")
    private String suitability;
    private final MockMvc mockMvc;
    private MockHttpServletRequestBuilder requestBuilder;

    @Autowired
    private AppUserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Nested
    @DisplayName(value = "Тесты на получение личного пользователя")
    class GetOwnTest {
        private RequestAppUserDto userDto;
        private String token;

        @BeforeEach
        void setUp() throws Exception {
            this.userDto = RequestAppUserDto.builder()
                    .login("Login123_.-")
                    .password("Qwerty12345!")
                    .build();

            MockHttpServletRequestBuilder registrationRequest = MockMvcRequestBuilders
                    .post("/api/auth/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));
            mockMvc.perform(registrationRequest);

            MockHttpServletRequestBuilder tokenRequest = MockMvcRequestBuilders
                    .post("/api/auth/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));
            token = mockMvc.perform(tokenRequest)
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            requestBuilder = MockMvcRequestBuilders
                    .get(uri + "/own")
                    .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @Description(value = "Тест на получение пользователя")
        void AppUser_getOwnSuccessTest() throws Exception {
            //given
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.login").value(userDto.getLogin()))
                    .andExpect(
                            jsonPath("$.roles[0]").value("USER"));
        }

        @Test
        @Description(value = "Тест на получение пользователя токеном null")
        void AppUser_getOwnNullToken() throws Exception {
            //given
            token = null;
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden());
        }

        @Test
        @Description(value = "Тест на получение пользователя токеном пустым")
        void AppUser_getOwnNoToken() throws Exception {
            //given
            token = "";
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden());
        }

        @Test
        @Description(value = "Тест на получение пользователя токеном с пробелами")
        void AppUser_getOwnBlankToken() throws Exception {
            //given
            token = "     ";
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden());
        }

        @Test
        @Description(value = "Тест на получение пользователя токеном неверным")
        void AppUser_getOwnWrongToken() throws Exception {
            //given
            StringBuilder token = new StringBuilder();
            Random random = new Random();
            StringBuilder password = new StringBuilder();
            for (int i = 0; i < 213; i++) {
                token.append(Character.toString('A' + random.nextInt(26)));
            }
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden());
        }

        @Test
        @Description(value = "Тест на получение пользователя токеном просроченным")
        void AppUser_getOwnExpiredToken() throws Exception {
            //given
            Thread.sleep(1000L * 60 * Integer.parseInt(suitability));
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden());
        }
    }
}
