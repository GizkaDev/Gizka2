package ru.gizka.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Random;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class HeroControllerTest {
    private String uri = "/api/user/hero";
    private RequestAppUserDto userDto;
    private RequestHeroDto heroDto;
    private RequestRaceDto raceDto;
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
        raceDto = RequestRaceDto.builder()
                .name("Человек")
                .isPlayable(true)
                .build();
        heroDto = RequestHeroDto.builder()
                .name("NameЯ")
                .lastName("LastnameБ")
                .str(10)
                .dex(10)
                .con(10)
                .race(raceDto.getName())
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
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
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
                            jsonPath("$[0].status").value("ALIVE"))
                    .andExpect(
                            jsonPath("$[0].race").value(raceDto.getName()))
                    .andExpect(
                            jsonPath("$[0].minInit").isNumber())
                    .andExpect(
                            jsonPath("$[0].maxInit").isNumber())
                    .andExpect(
                            jsonPath("$[0].minAttack").isNumber())
                    .andExpect(
                            jsonPath("$[0].maxAttack").isNumber())
                    .andExpect(
                            jsonPath("$[0].minEvasion").isNumber())
                    .andExpect(
                            jsonPath("$[0].maxEvasion").isNumber())
                    .andExpect(
                            jsonPath("$[0].minPhysDamage").isNumber())
                    .andExpect(
                            jsonPath("$[0].maxPhysDamage").isNumber())
                    .andExpect(
                            jsonPath("$[0].maxHp").isNumber())
                    .andExpect(
                            jsonPath("$[0].currentHp").isNumber())
                    .andExpect(
                            jsonPath("$[0].currentCon").isNumber());
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
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "The token was expected to have 3 parts, but got 0."));
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
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
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
                            jsonPath("$.status").value("ALIVE"))
                    .andExpect(
                            jsonPath("$.race").value(raceDto.getName()))
                    .andExpect(
                            jsonPath("$.minInit").isNumber())
                    .andExpect(
                            jsonPath("$.maxInit").isNumber())
                    .andExpect(
                            jsonPath("$.minAttack").isNumber())
                    .andExpect(
                            jsonPath("$.maxAttack").isNumber())
                    .andExpect(
                            jsonPath("$.minEvasion").isNumber())
                    .andExpect(
                            jsonPath("$.maxEvasion").isNumber())
                    .andExpect(
                            jsonPath("$.minPhysDamage").isNumber())
                    .andExpect(
                            jsonPath("$.maxPhysDamage").isNumber())
                    .andExpect(
                            jsonPath("$.maxHp").isNumber())
                    .andExpect(
                            jsonPath("$.currentHp").isNumber())
                    .andExpect(
                            jsonPath("$.currentCon").isNumber());
        }

        @Test
        @Description(value = "Тест на создание героя со слишком маленькими характеристиками")
        void Hero_create_lackOfCharacteristicPoints() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            heroDto.setStr(9);

            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Использовано слишком мало очков.*")));
        }

        @Test
        @Description(value = "Тест на создание героя со слишком большими характеристиками")
        void Hero_create_excessOfCharacteristicPoints() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            heroDto.setStr(11);
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Использовано слишком много очков.*")));
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
            requestBuilder.header("Authorization", "Bearer " + wrongToken);
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "The token was expected to have 3 parts, but got 0."));
        }

        @Test
        @Description(value = "Простое создание героя без авторизации")
        void Hero_create_Unauthorized() throws Exception {
            //given
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto));
            //when
            requestBuilder.header("Authorization", "Bearer " + null);
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isForbidden())
                    .andExpect(
                            header().string("Reason", "The token was expected to have 3 parts, but got 0."));
        }

        @Test
        @Description(value = "Создание героя с недопустимыми знаками в имени")
        void Hero_create_WrongName() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            heroDto.setName("%6732.?");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Имя героя может состоять только из букв русского или латинского алфавита.*")));
        }

        @Test
        @Description(value = "Создание героя с недопустимыми знаками в фамилии")
        void Hero_create_WrongLastname() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            heroDto.setLastName("%6732.?");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Фамилия героя может состоять только из букв русского или латинского алфавита.*")));
        }

        @Test
        @Description(value = "Создание героя с пустыми именем и фамилией")
        void Hero_create_EmptyLastname() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            heroDto.setLastName("");
            heroDto.setLastName("");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Фамилия героя должна состоять минимум из одного символа.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Фамилия героя может состоять только из букв русского или латинского алфавита.*")));
        }

        @Test
        @Description(value = "Создание героя с пробелами вместо имени и фамилии")
        void Hero_create_BlankLastname() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            heroDto.setLastName("     ");
            heroDto.setName("     ");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Фамилия героя должна состоять минимум из одного символа.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Имя героя может состоять только из букв русского или латинского алфавита.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Фамилия героя может состоять только из букв русского или латинского алфавита.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Имя героя должно состоять минимум из одного символа.*")));
        }

        @Test
        @Description(value = "Тест на создание героя с длинным именем")
        void Hero_create_LongName() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Имя героя должно состоять минимум из одного символа.*")));
        }

        @Test
        @Description(value = "Тест на создание героя с длинной фамилией")
        void Hero_create_LongLastname() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Фамилия героя должна состоять минимум из одного символа.*")));
        }

        @Test
        @Description(value = "Тест на создание героя с невалидными характеристиками")
        void Hero_create_LowCharacteristics() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Ловкость должна быть не меньше 5.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Выносливость должна быть не меньше 5.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Сила должна быть не меньше 5.*")));
        }

        @Test
        @Description(value = "Тест на создание героя с пустыми характеристиками")
        void Hero_create_NullCharacteristics() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Ловкость должна быть не меньше 5.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Выносливость должна быть не меньше 5.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Сила должна быть не меньше 5.*")));
        }

        @Test
        @Description(value = "Создание еще одного героя со статусом ALIVE")
        void Hero_create_secondAliveHero() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*У пользователя: Login123_.- есть герой: NameЯ LastnameБ со статусом ALIVE.*")));
        }

        @Test
        @Description(value = "Тест на создание героя без расы empty")
        void Hero_create_NoRace() throws Exception {
            //given
            heroDto.setRace("");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы должно состоять минимум из одного символа.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Раса не выбрана.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Использована несуществующая раса.*")));


        }

        @Test
        @Description(value = "Тест на создание героя без расы blank")
        void Hero_create_BlankRace() throws Exception {
            //given
            heroDto.setRace("    ");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Раса не выбрана.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Использована несуществующая раса.*")));
        }

        @Test
        @Description(value = "Тест на создание героя без расы null")
        void Hero_create_NullRace() throws Exception {
            //given
            heroDto.setRace(null);
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Раса не выбрана.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Использована несуществующая раса.*")));
        }

        @Test
        @Description(value = "Тест на создание героя с несуществующей расой")
        void Hero_create_WrongRace() throws Exception {
            //given
            heroDto.setRace("Лошадь");
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Использована несуществующая раса.*")));
        }

        @Test
        @Description(value = "Тест на создание героя с неиграбельной расой")
        void Hero_create_NoPlayableRace() throws Exception {
            //given
            RequestRaceDto noPlayable = RequestRaceDto.builder()
                    .name("Скелет")
                    .isPlayable(false)
                    .build();
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(noPlayable));
            heroDto.setRace(noPlayable.getName());
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Использована неиграбельная раса.*")));
        }
    }
}
