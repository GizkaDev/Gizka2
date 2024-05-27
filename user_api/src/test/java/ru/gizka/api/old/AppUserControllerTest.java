package ru.gizka.api.old;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.appUser.RequestAppUserDto;

import java.util.Random;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AppUserControllerTest {
    private String uri = "/api/user";
    @Value("${jwt.suitability.seconds}")
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
            this.userDto = new RequestAppUserDto(
                    "Login123_.-",
                    "Qwerty12345!");

            MockHttpServletRequestBuilder registrationRequest = MockMvcRequestBuilders
                    .post("/api/user/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));
            mockMvc.perform(registrationRequest);

            MockHttpServletRequestBuilder tokenRequest = MockMvcRequestBuilders
                    .post("/api/user/token")
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
                            header().string("Reason", "Перехвачен пустой токен"));
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
                            header().string("Reason", "Перехвачен пустой токен"));
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
            Thread.sleep(1000L * Integer.parseInt(suitability));
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
            this.userDto = new RequestAppUserDto(
                    "Login123_.-",
                    "Qwerty12345!");

            MockHttpServletRequestBuilder registrationRequest = MockMvcRequestBuilders
                    .post("/api/user/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));
            mockMvc.perform(registrationRequest);

            MockHttpServletRequestBuilder tokenRequest = MockMvcRequestBuilders
                    .post("/api/user/token")
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
                            header().string("Reason", "Перехвачен пустой токен"));
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
                            header().string("Reason", "Перехвачен пустой токен"));
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
            Thread.sleep(1000L * Integer.parseInt(suitability));
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
            this.userDto = new RequestAppUserDto(
                    "Login123_.-",
                    "Qwerty12345!");

            MockHttpServletRequestBuilder registrationRequest = MockMvcRequestBuilders
                    .post("/api/user/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));
            mockMvc.perform(registrationRequest);

            MockHttpServletRequestBuilder tokenRequest = MockMvcRequestBuilders
                    .post("/api/user/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));
            token = mockMvc.perform(tokenRequest)
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            requestBuilder = MockMvcRequestBuilders
                    .patch(uri + "/own")
                    .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @Description(value = "Тест на верификацию администратора")
        void AppUser_verifyAdmin_SuccessTest() throws Exception {
            //given
            requestBuilder
                    .content("ADMIN")
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

    @Nested
    @DisplayName(value = "Тесты на создание пользователя")
    class CreateTest {
        private RequestAppUserDto userDto;

        @BeforeEach
        void setUp() {
            this.userDto = new RequestAppUserDto(
                    "Login123_.-",
                    "Qwerty12345!");

            requestBuilder = MockMvcRequestBuilders
                    .post(uri + "/registration")
                    .contentType(MediaType.APPLICATION_JSON);
        }

        @Test
        @Description(value = "Тест на успешность регистрации нового пользователя")
        void Registration_CreationAppUserSuccessTest() throws Exception {
            //given
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isCreated())
                    .andExpect(
                            jsonPath("$.login").value(userDto.getLogin()))
                    .andExpect(
                            jsonPath("$.roles[0]").value("USER"));
        }

        @Test
        @Description(value = "Тест на уникальность логина")
        void Registration_LoginUniqueTest() throws Exception {
            //given
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            mockMvc.perform(requestBuilder);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Логин занят.*")));
        }

        @Test
        @Description(value = "Тест на пустой логин.")
        void Registration_EmptyLoginTest() throws Exception {
            //given
            this.userDto.setLogin("");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Логин должен состоять минимум из 4 символов.*")));
        }

        @Test
        @Description(value = "Тест на пробелы вместо логина")
        void Registration_BlankLoginTest() throws Exception {
            //given
            this.userDto.setLogin("      ");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Логин должен состоять минимум из 4 символов.*")));
        }

        @Test
        @Description(value = "Тест на проверку валидности длинного логина.")
        void Registration_LongLoginTest() throws Exception {
            //given
            Random random = new Random();
            StringBuilder login = new StringBuilder();
            for (int i = 0; i < 256; i++) {
                login.append(Character.toString('A' + random.nextInt(26)));
            }
            this.userDto.setLogin(login.toString());
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Логин должен состоять минимум из 4 символов.*")));
        }

        @Test
        @Description(value = "Тест на проверку не латинских букв в логине.")
        void Registration_LoginOnlyLatinLettersTest() throws Exception {
            //given
            this.userDto.setLogin("LoginЯй123_.-");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Логин может состоять только из букв латинского алфавита, цифр и специальных символов.*")));
        }

        @Test
        @Description(value = "Тест на проверку валидности короткого логина.")
        void Registration_ShortLoginTest() throws Exception {
            //given
            this.userDto.setLogin("Lo1");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Логин должен состоять минимум из 4 символов.*")));
        }

        @Test
        @Description(value = "Тест на короткий пароль.")
        void Registration_ShortPasswordTest() throws Exception {
            //given
            this.userDto.setPassword("Qwert1!");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Пароль должен состоять минимум из 8 символов.*")));
        }

        @Test
        @Description(value = "Тест на длинный пароль.")
        void Registration_LongPasswordTest() throws Exception {
            //given
            Random random = new Random();
            StringBuilder password = new StringBuilder();
            for (int i = 0; i < 256; i++) {
                password.append(Character.toString('A' + random.nextInt(26)));
            }
            this.userDto.setPassword(password.toString());
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Пароль должен состоять минимум из 8 символов.*")));
        }

        @Test
        @Description(value = "Тест на отсутствие заглавных букв в пароле.")
        void Registration_NoUpperCasePasswordTest() throws Exception {
            //given
            this.userDto.setPassword("qwerty1234!");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Пароль должен состоять из букв латинского алфавита в верхнем и нижнем регистре, цифр, а так же из специальных символов.*")));
        }

        @Test
        @Description(value = "Тест на отсутствие незаглавных букв в пароле.")
        void Registration_OnlyUpperCasePasswordTest() throws Exception {
            //given
            this.userDto.setPassword("QWERTY1234!");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Пароль должен состоять из букв латинского алфавита в верхнем и нижнем регистре, цифр, а так же из специальных символов.*")));
        }

        @Test
        @Description(value = "Тест на отсутствие букв в пароле.")
        void Registration_OnlyNumbersPasswordTest() throws Exception {
            //given
            this.userDto.setPassword("12345678!");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Пароль должен состоять из букв латинского алфавита в верхнем и нижнем регистре, цифр, а так же из специальных символов.*")));
        }

        @Test
        @Description(value = "Тест, если в пароле присутствуют неразрешенные символы.")
        void Registration_InvalidSymbolsPasswordTest() throws Exception {
            //given
            this.userDto.setPassword("Qйцу1234!._");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Пароль должен состоять из букв латинского алфавита в верхнем и нижнем регистре, цифр, а так же из специальных символов.*")));
        }

        @Test
        @Description(value = "Тест на отсутствие специальных символов в пароле.")
        void Registration_NoSpecialSymbolsPasswordTest() throws Exception {
            //given
            this.userDto.setPassword("Qwerty1234");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("jakarta.validation.ValidationException"))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Пароль должен состоять из букв латинского алфавита в верхнем и нижнем регистре, цифр, а так же из специальных символов.*")));
        }

        @Test
        @Description(value = "Тест на повторную регистрацию аутентифицированного пользователя")
        void Registration_DoubleRegistration() throws Exception {
            //given
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            mockMvc.perform(requestBuilder);
            RequestBuilder tokenRequest = MockMvcRequestBuilders
                    .post(uri + "/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));
            MvcResult mvcResult = mockMvc.perform(tokenRequest).andReturn();
            String token = mvcResult.getResponse().getContentAsString();

            this.userDto.setLogin("AnotherLogin");
            requestBuilder = MockMvcRequestBuilders
                    .post(uri + "/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden());
        }
    }

    @Nested
    @DisplayName(value = "Тесты на получение токена")
    class GetTokenTest {
        private RequestAppUserDto userDto;

        @BeforeEach
        void setUp() throws Exception {
            this.userDto = new RequestAppUserDto(
                    "Login123_.-",
                    "Qwerty12345!");

            requestBuilder = MockMvcRequestBuilders.post(uri + "/token")
                    .contentType(MediaType.APPLICATION_JSON);

            MockHttpServletRequestBuilder registrationRequest = MockMvcRequestBuilders
                    .post(uri + "/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(this.userDto));
            mockMvc.perform(registrationRequest);
        }

        @Test
        @Description(value = "Тест на успешность получения токена")
        void Token_getTokenSuccess() throws Exception {
            //given
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            Integer length = mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString()
                    .length();
            assertEquals(212, length);
        }

        @Test
        @Description(value = "Тест на получение токена без учетной записи")
        void Token_noBody() throws Exception {
            //given
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.exception").value("org.springframework.http.converter.HttpMessageNotReadableException"))
                    .andExpect(
                            jsonPath("$.descr").value(startsWith("Required request body is missing:")));
        }

        @Test
        @Description(value = "Тест на получение токена с неверным логином")
        void Token_getTokenWrongLogin() throws Exception {
            //given
            this.userDto.setLogin("WrongLogin");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            jsonPath("$.exception").value("org.springframework.security.authentication.InternalAuthenticationServiceException"))
                    .andExpect(
                            jsonPath("$.descr").value(String.format("Неверные учетные данные: %s", userDto.getLogin())));
        }

        @Test
        @Description(value = "Тест на получение токена с неверным паролем")
        void Token_getTokenWrongPassword() throws Exception {
            //given
            this.userDto.setPassword("WrongPassword");
            requestBuilder.content(new ObjectMapper().writeValueAsString(this.userDto));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            jsonPath("$.exception").value("org.springframework.security.authentication.BadCredentialsException"))
                    .andExpect(
                            jsonPath("$.descr").value("Bad credentials"));
        }
    }
}
