package ru.gizka.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
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
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.dto.hero.RequestHeroDto;

import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.gizka.api.RequestParentTest.requestWithTokenCheckForbidden;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class HeroControllerTest {
    private String uri = "/api/user/hero";
    private RequestAppUserDto userDto;
    private RequestHeroDto heroDto;
    private String token;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder requestBuilder;

    @Autowired
    private HeroControllerTest(MockMvc mockMvc,
                               ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() throws Exception {
        heroDto = RequestHeroDto.builder()
                .name("NameЯ")
                .lastName("LastnameБ")
                .str(10)
                .dex(10)
                .con(10)
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
    @DisplayName(value = "Тесты на получение текущего героя пользователя")
    class HeroCurrentTest {

        @BeforeEach
        void setUp() throws Exception {
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            mockMvc.perform(requestBuilder);
        }

        @Test
        @Description(value = "Тест на получение текущего героя пользователя")
        void Hero_getCurrentHero_Success() throws Exception {
            //given
            RequestBuilder getCurrentHeroRequest = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(getCurrentHeroRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$[0].name").value(heroDto.getName()))
                    .andExpect(
                            jsonPath("$[0].lastname").value(heroDto.getLastName()))
                    .andExpect(
                            jsonPath("$[0].str").value(heroDto.getStr()))
                    .andExpect(
                            jsonPath("$[0].dex").value(heroDto.getDex()))
                    .andExpect(
                            jsonPath("$[0].con").value(heroDto.getCon()))
                    .andExpect(
                            jsonPath("$[0].createdAt").value(Matchers.not(Matchers.empty())))
                    .andExpect(
                            jsonPath("$[0].userLogin").value(userDto.getLogin()))
                    .andExpect(
                            jsonPath("$[0].status").value("ALIVE"));
        }

        @Test
        @Description(value = "Тест на получение текущего героя c неверным логином и паролем")
        void Hero_getCurrentHero_WrongLoginPassword() throws Exception {
            //given
            StringBuilder wrongToken = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 213; i++) {
                wrongToken.append(Character.toString('A' + random.nextInt(26)));
            }
            requestBuilder = MockMvcRequestBuilders
                    .get("/game/user/hero")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", wrongToken));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden());
        }

        @Test
        @Description(value = "Тест на получение текущего героя без авторизации.")
        void Hero_getById_Unauthorized() throws Exception {
            //given
            requestBuilder = MockMvcRequestBuilders
                    .get("/game/user/hero")
                    .contentType(MediaType.APPLICATION_JSON);
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isForbidden());
        }
    }

    @Nested
    @DisplayName(value = "Тесты на создание героя")
    class HeroCreateTest {

        @Test
        @Description(value = "Простое создание героя, в том числе проверка на создание героя, если у пользователя до этого нет героя со статусом ALIVE")
        void Hero_create_Success() throws Exception {
            //given
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isCreated())
                    .andExpect(
                            jsonPath("$.name").value(heroDto.getName()))
                    .andExpect(
                            jsonPath("$.lastname").value(heroDto.getLastName()))
                    .andExpect(
                            jsonPath("$.str").value(heroDto.getStr()))
                    .andExpect(
                            jsonPath("$.dex").value(heroDto.getDex()))
                    .andExpect(
                            jsonPath("$.con").value(heroDto.getCon()))
                    .andExpect(
                            jsonPath("$.createdAt").value(Matchers.not(Matchers.empty())))
                    .andExpect(
                            jsonPath("$.userLogin").value(userDto.getLogin()))
                    .andExpect(
                            jsonPath("$.status").value("ALIVE"));
        }

        @Test
        @Description(value = "Тест на создание героя со слишком маленькими характеристиками")
        void Hero_create_lackOfCharacteristicPoints() throws Exception {
            //given
            heroDto.setStr(9);

            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание героя со слишком большими характеристиками")
        void Hero_create_excessOfCharacteristicPoints() throws Exception {
            //given
            heroDto.setStr(11);
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Простое создание героя c неверным токеном")
        void Hero_create_WrongPasswordLogin() throws Exception {
            //given
            StringBuilder wrongToken = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 213; i++) {
                wrongToken.append(Character.toString('A' + random.nextInt(26)));
            }
            requestBuilder.content(objectMapper.writeValueAsString(heroDto));
            requestWithTokenCheckForbidden(wrongToken.toString(), requestBuilder, mockMvc);
        }

        @Test
        @Description(value = "Простое создание героя без авторизации")
        void Hero_create_Unauthorized() throws Exception {
            //given
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto));
            //when
            requestWithTokenCheckForbidden(null, requestBuilder, mockMvc);
        }

        @Test
        @Description(value = "Создание героя с недопустимыми знаками в имени")
        void Hero_create_WrongName() throws Exception {
            //given
            heroDto.setName("%6732.?");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Создание героя с недопустимыми знаками в фамилии")
        void Hero_create_WrongLastname() throws Exception {
            //given
            heroDto.setLastName("%6732.?");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Создание героя с пустыми именем и фамилией")
        void Hero_create_EmptyLastname() throws Exception {
            //given
            heroDto.setLastName("");
            heroDto.setLastName("");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Создание героя с пробелами вместо имени и фамилии")
        void Hero_create_BlankLastname() throws Exception {
            //given
            heroDto.setLastName("     ");
            heroDto.setName("     ");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание героя с длинным именем")
        void Hero_create_LongName() throws Exception {
            //given
            Random random = new Random();
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 51; i++) {
                name.append(Character.toString('A' + random.nextInt(26)));
            }
            heroDto.setName(name.toString());
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание героя с длинной фамилией")
        void Hero_create_LongLastname() throws Exception {
            //given
            Random random = new Random();
            StringBuilder lastname = new StringBuilder();
            for (int i = 0; i < 101; i++) {
                lastname.append(Character.toString('A' + random.nextInt(26)));
            }
            heroDto.setLastName(lastname.toString());
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание героя с невалидными характеристиками")
        void Hero_create_LowCharacteristics() throws Exception {
            //given
            heroDto.setStr(4);
            heroDto.setDex(4);
            heroDto.setCon(4);
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Тест на создание героя с пустыми характеристиками")
        void Hero_create_NullCharacteristics() throws Exception {
            //given
            heroDto.setStr(null);
            heroDto.setDex(null);
            heroDto.setCon(null);
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }

        @Test
        @Description(value = "Создание еще одного героя со статусом ALIVE")
        void Hero_create_secondAliveHero() throws Exception {
            //given
            RequestBuilder twinkHeroCreationRequest = MockMvcRequestBuilders
                    .post("/api/user/hero")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            mockMvc.perform(twinkHeroCreationRequest);

            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest());
        }
    }
}
