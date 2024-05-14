package ru.gizka.api.item.armor;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.gizka.api.RequestParentTest;
import ru.gizka.api.dto.item.RequestProductDto;
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
public class AdminArmorPatternControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder createRequest;
    private Random random;
    private String uri = "/api/admin/armor";

    @Autowired
    private AdminArmorPatternControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @Nested
    @DisplayName(value = "Тесты на создание нового шаблона доспеха")
    class CreateTest {
        private RequestArmorPatternDto armorDto;
        private RequestAppUserDto userDto;
        private RequestProductDto productDto;

        @BeforeEach
        void setUp() throws Exception {
            createRequest = MockMvcRequestBuilders
                    .post(uri)
                    .contentType(MediaType.APPLICATION_JSON);

            userDto = RequestAppUserDto.builder()
                    .login("Biba")
                    .password("Qwerty12345!")
                    .build();

            armorDto = new RequestArmorPatternDto(
                    "Кольчуга",
                    10000L,
                    2,
                    3,
                    -2,
                    ArmorType.MEDIUM.toString());

            productDto = new RequestProductDto(
                    "Доспехи",
                    10000);
        }

        @Test
        @Description(value = "Тест на создание шаблона доспеха")
        void ArmorPattern_createSuccess() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isCreated())
                    .andExpect(
                            jsonPath("$.name").value(armorDto.getName()))
                    .andExpect(
                            jsonPath("$.armor").value(armorDto.getArmor()))
                    .andExpect(
                            jsonPath("$.productDto.name").value(productDto.getName()))
                    .andExpect(
                            jsonPath("$.dexPenalty").value(armorDto.getDexPenalty()))
                    .andExpect(
                            jsonPath("$.armorType").value(armorDto.getArmorType()));
        }

        @Test
        @Description(value = "Тест на создание шаблона доспехов с длинным названием")
        void Armor_create_LongName() throws Exception {
            //given
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 201; i++) {
                name.append(Character.toString('А' + random.nextInt(33)));
            }
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            armorDto.setName(name.toString());
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название шаблона предмета должно состоять из 1-200 символов")));
        }

        @Test
        @Description(value = "Тест на создание шаблона предмета без названия")
        void Armor_create_NulltName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            armorDto.setName(null);
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название шаблона предмета должно состоять из 1-200 символов")));
        }

        @Test
        @Description(value = "Тест на создание шаблона доспеха без названия")
        void Armor_create_BlankName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            armorDto.setName("       ");
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название шаблона предмета должно состоять из 1-200 символов")));
        }

        @Test
        @Description(value = "Тест на создание доспеха предмета с уникальным названием")
        void Armor_create_UniqueName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(armorDto), token);
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название шаблона доспеха уже существует")));
        }

        @Test
        @Description(value = "Тест на создание шаблона доспеха с недопустимым названием")
        void Armor_create_NotAllowName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertArmorPattern(mockMvc, objectMapper.writeValueAsString(armorDto), token);
            armorDto.setName("null");
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Недопустимое название")));
        }

        @Test
        @Description(value = "Тест на создание шаблона доспеха без прав администратора")
        void Armor_create_NoAdmin() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            armorDto.setName("null");
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isForbidden());
        }

        @Test
        @Description(value = "Тест на создание шаблона доспеха с отрицательной броней")
        void Armor_create_NegativeArmor() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            armorDto.setArmor(-1);
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Значение брони должно быть 0 или больше")));
        }

        @Test
        @Description(value = "Тест на создание шаблона доспеха с положительным штрафом на ловкость")
        void Armor_create_PositiveDexPenalty() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            armorDto.setDexPenalty(2);
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Значение штрафа к ловкости должно быть 0 или меньше")));
        }

        @Test
        @Description(value = "Тест на создание шаблона доспеха с несуществующим типом")
        void Armor_create_WrongType() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            armorDto.setArmorType("MITHRIL");
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Недействительный тип доспехов")));
        }

        @Test
        @Description(value = "Тест на создание шаблона доспеха без товара Доспехи")
        void ArmorPattern_NoProduct() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsString(armorDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Использован несуществующий товар")));
        }
    }
}
