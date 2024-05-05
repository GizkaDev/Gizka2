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
import ru.gizka.api.dto.creature.RequestCreatureDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.hero.ResponseHeroDto;
import ru.gizka.api.dto.race.RequestRaceDto;
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
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
        raceDto = new RequestRaceDto("Человек", true,
                0, 0, 0, 0);

        heroDto = RequestHeroDto.builder()
                .name("NameЯ")
                .lastName("LastnameБ")
                .str(10)
                .dex(10)
                .con(10)
                .wis(10)
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
                            jsonPath("$[0].wis").value(heroDto.getWis()))
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
                            jsonPath("$.wis").value(heroDto.getWis()))
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
            heroDto.setWis(4);
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
                            jsonPath("$.descr").value(matchesPattern(".*Телосложение должно быть не меньше 5.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Сила должна быть не меньше 5.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Мудрость должна быть не меньше 5.*")));
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
            heroDto.setWis(null);
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
                            jsonPath("$.descr").value(matchesPattern(".*Телосложение должно быть не меньше 5.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Сила должна быть не меньше 5.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Мудрость должна быть не меньше 5.*")));
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
            RequestRaceDto noPlayable = new RequestRaceDto("Скелет", false,
                    0, 0, 0, 0);
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

        @Test
        @Description(value = "Создание еще одного героя после смерти первого без ожидания")
        void Hero_create_secondHeroAfterDeath() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(RequestCreatureDto.builder()
                    .name("Примарх")
                    .str(1000)
                    .dex(1000)
                    .con(1000)
                    .race(raceDto.getName())
                    .build()));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertFight(mockMvc, "Примарх", token1);

            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));

            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Прошло мало времени с момента смерти последнего героя.*")));
        }

        @Test
        @Description(value = "Создание еще одного героя после смерти первого с ожиданием")
        void Hero_create_secondHeroAfterDeathWithWait() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(RequestCreatureDto.builder()
                    .name("Примарх")
                    .str(1000)
                    .dex(1000)
                    .con(1000)
                    .race(raceDto.getName())
                    .build()));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertFight(mockMvc, "Примарх", token1);

            Thread.sleep(20 * 1000);

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
                            jsonPath("$.wis").value(heroDto.getWis()))
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
        @Description(value = "Простое создание героя, в том числе проверка на создание героя, если у пользователя до этого нет героя со статусом ALIVE c расой с активными бонусами")
        void Hero_create_SuccessWithRaceBonuses() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            raceDto = new RequestRaceDto("Мутант", true,
                    -3, -2, 2, 3);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            heroDto.setRace(raceDto.getName());
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
                            jsonPath("$.str").value(heroDto.getStr() + raceDto.getStrBonus()))
                    .andExpect(
                            jsonPath("$.dex").value(heroDto.getDex() + raceDto.getDexBonus()))
                    .andExpect(
                            jsonPath("$.con").value(heroDto.getCon() + raceDto.getConBonus()))
                    .andExpect(
                            jsonPath("$.wis").value(heroDto.getWis() + raceDto.getWisBonus()))
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
        @Description(value = "Простое создание героя, в том числе проверка на создание героя, если у пользователя до этого нет героя со статусом ALIVE " +
                "c расой с активными бонусами, но с суммой меньше 5")
        void Hero_create_SuccessWithRaceBonusesLowSum() throws Exception {
            //given
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token1);
            raceDto = new RequestRaceDto("Мутант", true,
                    -1, -2, -3, -4);
            RequestParentTest.insertRace(mockMvc, token1, objectMapper.writeValueAsString(raceDto));
            heroDto = new RequestHeroDto("Гуль", "Борисович",
                    5, 6, 7, 22, raceDto.getName());
            heroDto.setRace(raceDto.getName());
            requestBuilder
                    .content(objectMapper.writeValueAsString(heroDto))
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(requestBuilder)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Сила должна быть не меньше 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Ловкость должна быть не меньше 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Телосложение должно быть не меньше 5")));
        }
    }

    @Nested
    @DisplayName(value = "Тесты на лечение")
    class HeroTreatTest {

        private RequestCreatureDto creatureDto;

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
        @Description(value = "Тест на лечение")
        void Hero_treat_Success() throws Exception {
            //given
            creatureDto = RequestCreatureDto.builder()
                    .name("Ниндзя")
                    .str(10)
                    .con(2)
                    .dex(100)
                    .race(raceDto.getName())
                    .build();

            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertFight(mockMvc, "Ниндзя", token);
            String stringResponse = RequestParentTest.getCurrentHero(mockMvc, token).andReturn().getResponse().getContentAsString();
            ResponseHeroDto[] responseHero = objectMapper.readValue(stringResponse, ResponseHeroDto[].class);

            RequestBuilder getTreatRequest = MockMvcRequestBuilders
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            ResponseHeroDto afterHeal = objectMapper.readValue(mockMvc.perform(getTreatRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto.class);
            assertTrue(afterHeal.getCurrentHp() >= responseHero[0].getCurrentHp());
            assertTrue(afterHeal.getCurrentHp() <= afterHeal.getMaxHp());
        }

        @Test
        @Description(value = "Тест на лечение, если лечение может превысить maxHp")
        void Hero_treat_OverTreat() throws Exception {
            //given
            creatureDto = RequestCreatureDto.builder()
                    .name("Ниндзя")
                    .str(1)
                    .con(1)
                    .dex(100)
                    .race(raceDto.getName())
                    .build();

            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertFight(mockMvc, "Ниндзя", token);
            String stringResponse = RequestParentTest.getCurrentHero(mockMvc, token).andReturn().getResponse().getContentAsString();
            ResponseHeroDto[] responseHero = objectMapper.readValue(stringResponse, ResponseHeroDto[].class);

            RequestBuilder getTreatRequest = MockMvcRequestBuilders
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            ResponseHeroDto afterHeal = objectMapper.readValue(mockMvc.perform(getTreatRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto.class);
            assertTrue(afterHeal.getCurrentHp() >= responseHero[0].getCurrentHp());
            assertTrue(afterHeal.getCurrentHp() <= afterHeal.getMaxHp());
            assertEquals(afterHeal.getCurrentHp(), responseHero[0].getMaxHp());
        }

        @Test
        @Description(value = "Тест на лечение, если урон больше лечения")
        void Hero_treat_OverHit() throws Exception {
            //given
            creatureDto = RequestCreatureDto.builder()
                    .name("Ниндзя")
                    .str(29)
                    .con(1)
                    .dex(100)
                    .race(raceDto.getName())
                    .build();

            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertFight(mockMvc, "Ниндзя", token);
            String stringResponse = RequestParentTest.getCurrentHero(mockMvc, token).andReturn().getResponse().getContentAsString();
            ResponseHeroDto[] responseHero = objectMapper.readValue(stringResponse, ResponseHeroDto[].class);

            RequestBuilder getTreatRequest = MockMvcRequestBuilders
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            ResponseHeroDto afterHeal = objectMapper.readValue(mockMvc.perform(getTreatRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto.class);
            assertTrue(afterHeal.getCurrentHp() >= responseHero[0].getCurrentHp());
            assertTrue(afterHeal.getCurrentHp() <= afterHeal.getMaxHp());
            assertEquals(Math.min(responseHero[0].getCurrentHp() + responseHero[0].getWis(), responseHero[0].getMaxHp()), (int) afterHeal.getCurrentHp());
        }

        @Test
        @Description(value = "Тест на лечение, если лечение не требуется")
        void Hero_treat_NoNeed() throws Exception {
            //given
            String stringResponse = RequestParentTest.getCurrentHero(mockMvc, token).andReturn().getResponse().getContentAsString();
            ResponseHeroDto[] responseHero = objectMapper.readValue(stringResponse, ResponseHeroDto[].class);

            RequestBuilder getTreatRequest = MockMvcRequestBuilders
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            ResponseHeroDto afterHeal = objectMapper.readValue(mockMvc.perform(getTreatRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto.class);
            assertSame(afterHeal.getCurrentHp(), responseHero[0].getCurrentHp());
            assertSame(afterHeal.getCurrentHp(), responseHero[0].getMaxHp());
        }

        @Test
        @Description(value = "Тест на лечение, если герой DEAD")
        void Hero_treat_Dead() throws Exception {
            //given
            creatureDto = RequestCreatureDto.builder()
                    .name("Примарх")
                    .str(1000)
                    .con(1000)
                    .dex(1000)
                    .race(raceDto.getName())
                    .build();

            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertFight(mockMvc, creatureDto.getName(), token);

            RequestBuilder getTreatRequest = MockMvcRequestBuilders
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(getTreatRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У пользователя нет героя со статусом ALIVE."));
        }

        @Test
        @Description(value = "Тест на лечение, если героя нет")
        void Hero_treat_NoHero() throws Exception {
            //given
            RequestAppUserDto appUserDto = new RequestAppUserDto("Biba", "Qwerty12345!");
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(appUserDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(appUserDto));

            RequestBuilder getTreatRequest = MockMvcRequestBuilders
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));
            //when
            mockMvc.perform(getTreatRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value("У пользователя нет героя со статусом ALIVE."));
        }

        @Test
        @Description(value = "Тест на лечение, если лечение уже было")
        void Hero_treat_Repeat() throws Exception {
            //given
            creatureDto = RequestCreatureDto.builder()
                    .name("Ниндзя")
                    .str(29)
                    .con(1)
                    .dex(100)
                    .race(raceDto.getName())
                    .build();

            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertFight(mockMvc, "Ниндзя", token);
            String stringResponse = RequestParentTest.getCurrentHero(mockMvc, token).andReturn().getResponse().getContentAsString();
            ResponseHeroDto[] responseHero = objectMapper.readValue(stringResponse, ResponseHeroDto[].class);
            RequestParentTest.treat(mockMvc, token);

            RequestBuilder getTreatRequest = MockMvcRequestBuilders
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            mockMvc.perform(getTreatRequest)
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr", Matchers.containsString("Перевязывать раны можно только раз в сутки. Время последнего применения")));
        }

        @Test
        @Description(value = "Тест на лечение, если лечение уже было после истечения срока")
        void Hero_treat_RepeatAfterTimer() throws Exception {
            //given
            creatureDto = RequestCreatureDto.builder()
                    .name("Ниндзя")
                    .str(29)
                    .con(1)
                    .dex(100)
                    .race(raceDto.getName())
                    .build();

            RequestParentTest.insertCreature(mockMvc, token, objectMapper.writeValueAsString(creatureDto));
            RequestParentTest.insertFight(mockMvc, "Ниндзя", token);
            String stringResponse = RequestParentTest.getCurrentHero(mockMvc, token).andReturn().getResponse().getContentAsString();
            ResponseHeroDto[] responseHero = objectMapper.readValue(stringResponse, ResponseHeroDto[].class);
            RequestParentTest.treat(mockMvc, token);

            RequestBuilder getTreatRequest = MockMvcRequestBuilders
                    .put(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token));

            Thread.sleep(20 * 1000);

            //when
            ResponseHeroDto afterHeal = objectMapper.readValue(mockMvc.perform(getTreatRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andReturn().getResponse().getContentAsString(), ResponseHeroDto.class);
            assertTrue(afterHeal.getCurrentHp() >= responseHero[0].getCurrentHp());
            assertTrue(afterHeal.getCurrentHp() <= afterHeal.getMaxHp());
        }
    }
}
