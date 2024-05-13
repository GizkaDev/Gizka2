package ru.gizka.api.item.armor;

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
import ru.gizka.api.dto.item.armor.RequestArmorPatternDto;
import ru.gizka.api.dto.user.RequestAppUserDto;
import ru.gizka.api.model.item.armor.ArmorType;

import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ArmorPatterControllerTest {
    private String uri = "/api/armor";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Random random;

    @Autowired
    private ArmorPatterControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @Nested
    @DisplayName(value = "Тесты на получение шаблона доспеха по названию")
    class GetByNameTest {
        private RequestAppUserDto userDto;
        private RequestArmorPatternDto armorDto;
        private MockHttpServletRequestBuilder getRequest;

        @BeforeEach
        void setUp() {
            userDto = RequestAppUserDto.builder()
                    .login("Biba")
                    .password("Qwerty12345!")
                    .build();

            armorDto = new RequestArmorPatternDto(
                    "Кольчуга",
                    3,
                    -2,
                    ArmorType.MEDIUM.toString());
        }

        @Test
        @Description(value = "Тест на получение шаблона доспеха")
        void Armor_getByName_SuccessTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(armorDto), token);
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + armorDto.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.name").value(armorDto.getName()))
                    .andExpect(
                            jsonPath("$.armor").value(armorDto.getArmor()))
                    .andExpect(
                            jsonPath("$.dexPenalty").value(armorDto.getDexPenalty()))
                    .andExpect(
                            jsonPath("$.armorType").value(armorDto.getArmorType()));
        }

        @Test
        @Description(value = "Тест на получение несуществующего шаблона доспеха")
        void Armor_getByName_NotExist() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + armorDto.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Шаблон не найден: " + armorDto.getName())));
        }

        @Test
        @Description(value = "Тест на получение шаблона доспеха null")
        void Armor_getByName_NullName() throws Exception {
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
                            jsonPath("$.descr").value(containsString("Шаблон не найден: ")));
        }

        @Test
        @Description(value = "Тест на получение шаблона доспеха empty")
        void Item_getByName_EmptyName() throws Exception {
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
                            jsonPath("$.descr").value(containsString("No static resource")));
        }

        @Test
        @Description(value = "Тест на получение шаблона доспеха blank")
        void Item_getByName_BlankName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + "        ")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Шаблон не найден: ")));
        }

        @Test
        @Description(value = "Тест на получение доспеха предмета длинным именем")
        void Item_getByName_LongName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 201; i++) {
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
}
