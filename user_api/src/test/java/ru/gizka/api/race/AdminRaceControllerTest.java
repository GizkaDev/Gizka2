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
import ru.gizka.api.model.race.RaceSize;

import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
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

            raceDto = new RequestRaceDto("Человек", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

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
                            jsonPath("$.isPlayable").value(raceDto.getIsPlayable()))
                    .andExpect(
                            jsonPath("$.raceSize").value(raceDto.getRaceSize()));
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы уже существует.*")));
        }

        @Test
        @Description(value = "Тест на создание расы без названия")
        void Race_create_NoName() throws Exception {
            //given
            raceDto = new RequestRaceDto("", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы должно состоять минимум из одного символа.*")));
        }

        @Test
        @Description(value = "Тест на создание расы без названия")
        void Race_create_NullName() throws Exception {
            //given
            raceDto = new RequestRaceDto(null, true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы должно состоять минимум из одного символа.*")));
        }

        @Test
        @Description(value = "Тест на создание расы без названия")
        void Race_create_BlankName() throws Exception {
            //given
            raceDto = new RequestRaceDto("      ", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы должно состоять минимум из одного символа.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы может состоять только из букв русского алфавита и тире.*")));
        }

        @Test
        @Description(value = "Тест на создание расы без названия")
        void Race_create_EmptyName() throws Exception {
            //given
            raceDto = new RequestRaceDto("", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы должно состоять минимум из одного символа.*")))
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы может состоять только из букв русского алфавита и тире.*")));
        }

        @Test
        @Description(value = "Тест на создание расы без играбельности")
        void Race_create_NoIsPlayable() throws Exception {
            //given
            raceDto = new RequestRaceDto("Человек", null,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Не указана играбельность расы.*")));
        }

        @Test
        @Description(value = "Тест на создание расы без играбельности")
        void Race_create_NullIsPlayable() throws Exception {
            //given
            raceDto = new RequestRaceDto("Человек", null,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Не указана играбельность расы.*")));
        }

        @Test
        @Description(value = "Тест на создание расы без прав администратора")
        void Race_create_NoAdmin() throws Exception {
            //given
            raceDto = new RequestRaceDto("Человек", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());
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
            raceDto = new RequestRaceDto(name.toString(), true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы должно состоять минимум из одного символа.*")));
        }

        @Test
        @Description(value = "Тест на создание расы c запрещенными символами")
        void Race_create_ForbiddenSymbols() throws Exception {
            //given
            raceDto = new RequestRaceDto("ЭльфZ", true,
                    0, 0, 0, 0, 0, RaceSize.AVERAGE.name());

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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(matchesPattern(".*Название расы может состоять только из букв русского алфавита и тире.*")));
        }

        @Test
        @Description(value = "Тест на создание расы c бонусом расы")
        void Race_createSuccess_WithBonus() throws Exception {
            //given
            raceDto = new RequestRaceDto("Гном", true, 1, -2, 0, 1, 0, RaceSize.AVERAGE.name());
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
                            jsonPath("$.isPlayable").value(raceDto.getIsPlayable()))
                    .andExpect(
                            jsonPath("$.strBonus").value(raceDto.getStrBonus()))
                    .andExpect(
                            jsonPath("$.dexBonus").value(raceDto.getDexBonus()))
                    .andExpect(
                            jsonPath("$.conBonus").value(raceDto.getConBonus()))
                    .andExpect(
                            jsonPath("$.wisBonus").value(raceDto.getWisBonus()))
                    .andExpect(
                            jsonPath("$.defBonus").value(raceDto.getDefBonus()));
        }

        @Test
        @Description(value = "Тест на создание расы c бонусом расы за пределами")
        void Race_createSuccess_WithTooBigBonus() throws Exception {
            //given
            raceDto = new RequestRaceDto("Гном", true, -6, 6, -6, 6, -1, RaceSize.AVERAGE.name());
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус силы должен быть в диапазоне от -5 до 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус ловкости должен быть в диапазоне от -5 до 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус телосложения должен быть в диапазоне от -5 до 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус мудрости должен быть в диапазоне от -5 до 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус защиты должен быть 0 или больше")));
        }

        @Test
        @Description(value = "Тест на создание расы c бонусом расы null")
        void Race_createSuccess_WithNullBonus() throws Exception {
            //given
            raceDto = new RequestRaceDto("Гном", true, null, null, null, null, null, RaceSize.AVERAGE.name());
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
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус силы должен быть в диапазоне от -5 до 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус ловкости должен быть в диапазоне от -5 до 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус телосложения должен быть в диапазоне от -5 до 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус мудрости должен быть в диапазоне от -5 до 5")))
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Бонус защиты должен быть 0 или больше")));
        }

        @Test
        @Description(value = "Тест на создание расы с недействительным размером")
        void Race_create_WrongSize() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            //when
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(new RequestRaceDto("Человек", true,
                            0, 0, 0, 0, 0, "EPIC")))
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Недействительный размер расы")));
        }

        @Test
        @Description(value = "Тест на создание расы с недействительным размером")
        void Race_create_NullSize() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            //when
            RequestParentTest.insertRace(mockMvc, token, objectMapper.writeValueAsString(new RequestRaceDto("Человек", true,
                            0, 0, 0, 0, 0, null)))
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Недействительный размер расы")));
        }
    }
}
