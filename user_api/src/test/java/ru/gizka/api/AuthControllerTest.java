package ru.gizka.api;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Random;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AuthControllerTest {
    private String uri = "/api/auth";
    private final MockMvc mockMvc;
    private MockHttpServletRequestBuilder requestBuilder;

    @Autowired
    private AuthControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Nested
    @DisplayName(value = "Тесты на регистрацию пользователя")
    class RegistrationTest {
        private RequestAppUserDto userDto;

        @BeforeEach
        void setUp() {
            this.userDto = RequestAppUserDto.builder()
                    .login("Login123_.-")
                    .password("Qwerty12345!")
                    .build();

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
    class TokenTest {
        private RequestAppUserDto userDto;

        @BeforeEach
        void setUp() throws Exception {
            this.userDto = RequestAppUserDto.builder()
                    .login("Login123_.-")
                    .password("Qwerty12345!")
                    .build();

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
