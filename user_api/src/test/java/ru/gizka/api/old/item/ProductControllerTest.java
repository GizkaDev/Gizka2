package ru.gizka.api.old.item;

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
import ru.gizka.api.old.RequestParentTest;
import ru.gizka.api.dto.old.item.RequestProductDto;
import ru.gizka.api.dto.appUser.RequestAppUserDto;

import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ProductControllerTest {
    private String uri = "/api/product";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Random random;

    @Autowired
    private ProductControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @Nested
    @DisplayName(value = "Тесты на получение товара по названию")
    class GetByNameTest {
        private RequestAppUserDto userDto;
        private RequestProductDto productDto;
        private MockHttpServletRequestBuilder getRequest;

        @BeforeEach
        void setUp() {
            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            productDto = new RequestProductDto(
                    "Оружие",
                    50L);
        }

        @Test
        @Description(value = "Тест на получение товара")
        void Product_getByName_SuccessTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + productDto.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$.name").value(productDto.getName()))
                    .andExpect(
                            jsonPath("$.price").value(productDto.getPrice()));
        }

        @Test
        @Description(value = "Тест на получение несуществующего товара")
        void Product_getByName_NotExist() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + productDto.getName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Товар не найден: " + productDto.getName())));
        }

        @Test
        @Description(value = "Тест на получение товара null")
        void Product_getByName_NullName() throws Exception {
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
                            jsonPath("$.descr").value(containsString("Товар не найден: " + null)));
        }

        @Test
        @Description(value = "Тест на получение товара empty")
        void Product_getByName_EmptyName() throws Exception {
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
        @Description(value = "Тест на получение товара blank")
        void Product_getByName_BlankName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/" + "     ")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isNotFound())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Товар не найден: ")));
        }

        @Test
        @Description(value = "Тест на получение товара с длинным именем")
        void Product_getByName_LongName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 101; i++) {
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

    @Nested
    @DisplayName(value = "Тесты на получение всех товаров")
    class GetAllTest {
        private RequestAppUserDto userDto;
        private RequestProductDto productDto1;
        private RequestProductDto productDto2;
        private RequestProductDto productDto3;
        private RequestProductDto productDto4;
        private RequestProductDto productDto5;
        private MockHttpServletRequestBuilder getRequest;

        @BeforeEach
        void setUp() {
            userDto = new RequestAppUserDto(
                    "Biba",
                    "Qwerty12345!");

            productDto1 = new RequestProductDto(
                    "Оружие",
                    50L);

            productDto2 = new RequestProductDto(
                    "Еда",
                    50L);

            productDto3 = new RequestProductDto(
                    "Роскошь",
                    50L);

            productDto4 = new RequestProductDto(
                    "Броня",
                    50L);

            productDto5 = new RequestProductDto(
                    "Одежда",
                    50L);
        }

        @Test
        @Description(value = "Тест на получение всех товаров")
        void Race_getAll_SuccessTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto1), token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto2), token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto3), token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto4), token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto5), token);
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$", hasSize(5)));
        }

        @Test
        @Description(value = "Тест на получение всех товаров, если нет товаров")
        void Race_getAll_NoRacesTest() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            getRequest = MockMvcRequestBuilders
                    .get(uri + "/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(getRequest)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$", hasSize(0)));
        }
    }
}
