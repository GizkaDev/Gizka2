package ru.gizka.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Random;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.gizka.api.RequestParentTest.*;

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
            requestBuilder.header("Authorization", "Bearer " + null);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "The token was expected to have 3 parts, but got 0."));
        }

        @Test
        @Description(value = "Тест на получение пользователя токеном пустым")
        void AppUser_getOwnNoToken() throws Exception {
            //given
            requestBuilder.header("Authorization", "Bearer " + "");
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "Фильтр перехватил пустой токен"));
        }

        @Test
        @Description(value = "Тест на получение пользователя токеном с пробелами")
        void AppUser_getOwnBlankToken() throws Exception {
            //given
            requestBuilder.header("Authorization", "Bearer " + "      ");
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "Фильтр перехватил пустой токен"));
        }

        @Test
        @Description(value = "Тест на получение пользователя токеном неверным")
        void AppUser_getOwnWrongToken() throws Exception {
            //given
            StringBuilder token = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 213; i++) {
                token.append(Character.toString('A' + random.nextInt(26)));
            }
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "The token was expected to have 3 parts, but got 0."));
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
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", startsWith("The Token has expired on")));
        }
    }

    @Nested
    @DisplayName(value = "Тесты на удаление личного пользователя")
    class DeleteOwnTest {
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
                    .delete(uri + "/own")
                    .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @Description(value = "Тест на удаление личного пользователя")
        void AppUser_deleteOwnSuccessTest() throws Exception {
            //given
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNoContent());
        }

        @Test
        @Description(value = "Тест проверку валидности токена после удаления")
        void AppUser_checkTokenAfterDelete() throws Exception {
            //given
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isNoContent());

            MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders
                    .get(uri + "/own")
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", startsWith("Пользователь не найден:")));
        }

        @Test
        @Description(value = "Тест на удаление пользователя токеном null")
        void AppUser_deleteOwnNullToken() throws Exception {
            //given
            requestBuilder.header("Authorization", "Bearer " + null);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "The token was expected to have 3 parts, but got 0."));
        }


        @Test
        @Description(value = "Тест на удаление пользователя токеном пустым")
        void AppUser_deleteOwnNoToken() throws Exception {
            //given
            requestBuilder.header("Authorization", "Bearer " + "");
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "Фильтр перехватил пустой токен"));
        }

        @Test
        @Description(value = "Тест на удаление пользователя токеном с пробелами")
        void AppUser_deleteOwnBlankToken() throws Exception {
            //given
            requestBuilder.header("Authorization", "Bearer " + "      ");
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "Фильтр перехватил пустой токен"));
        }

        @Test
        @Description(value = "Тест на удаление пользователя токеном неверным")
        void AppUser_deleteOwnWrongToken() throws Exception {
            StringBuilder token = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 213; i++) {
                token.append(Character.toString('A' + random.nextInt(26)));
            }
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "The token was expected to have 3 parts, but got 0."));
        }

        @Test
        @Description(value = "Тест на удаление пользователя токеном просроченным")
        void AppUser_deleteOwnExpiredToken() throws Exception {
            Thread.sleep(1000L * 60 * Integer.parseInt(suitability));
            requestBuilder.header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", startsWith("The Token has expired on")));
        }
    }

    @Nested
    @DisplayName(value = "Тесты на верификацию администратора")
    class VerifyAdminTest {
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
                    .put(uri + "/own")
                    .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @Description(value = "Тест на верификацию администратора")
        void AppUser_verifyAdmin_SuccessTest() throws Exception {
            //given
            requestBuilder
                    .content("SECRET")
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.login").value(userDto.getLogin()))
                    .andExpect(
                            jsonPath("$.roles", Matchers.containsInAnyOrder("USER", "ADMIN")));
        }

        @Test
        @Description(value = "Тест на верификацию администратора с неверным секретом")
        void AppUser_verifyAdmin_WrongSecretTest() throws Exception {
            //given
            requestBuilder
                    .content("NOT_SECRET")
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            jsonPath("$.descr").value("Верификация не пройдена"));
        }

        @Test
        @Description(value = "Тест на верификацию администратора с пустым секретом")
        void AppUser_verifyAdmin_BlankSecretTest() throws Exception {
            //given
            requestBuilder
                    .content("     ")
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            jsonPath("$.descr").value("Верификация не пройдена"));
        }

        @Test
        @Description(value = "Тест на верификацию администратора с пустым секретом")
        void AppUser_verifyAdmin_EmptySecretTest() throws Exception {
            //given
            requestBuilder
                    .content("")
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(startsWith("Required request body is missing:")));
        }

        @Test
        @Description(value = "Тест на верификацию администратора с пустым секретом")
        void AppUser_verifyAdmin_NoContentTest() throws Exception {
            //given
            requestBuilder
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(startsWith("Required request body is missing:")));
        }
    }
}
