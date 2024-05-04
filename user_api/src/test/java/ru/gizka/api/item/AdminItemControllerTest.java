package ru.gizka.api.item;

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
import ru.gizka.api.dto.item.RequestItemDto;
import ru.gizka.api.dto.item.RequestProductDto;
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AdminItemControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private MockHttpServletRequestBuilder createRequest;
    private Random random;
    private String uri = "/api/admin/item";

    @Autowired
    private AdminItemControllerTest(MockMvc mockMvc){
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
    }

    @Nested
    @DisplayName(value = "Тесты на создание нового предмета")
    class CreateTest {
        private RequestItemDto itemDto;
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

            productDto = RequestProductDto.builder()
                    .name("Роскошь")
                    .price(500)
                    .build();

            itemDto = RequestItemDto.builder()
                    .name("Золотой кубок")
                    .value(1)
                    .weight(1000L)
                    .product("Роскошь")
                    .build();
        }

        @Test
        @Description(value = "Тест на создание предмета")
        void Item_createSuccess() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isCreated())
                    .andExpect(
                            jsonPath("$.name").value(itemDto.getName()))
                    .andExpect(
                            jsonPath("$.value").value(itemDto.getValue()))
                    .andExpect(
                            jsonPath("$.weight").value(itemDto.getWeight()))
                    .andExpect(
                            jsonPath("$.productDto.name").value(productDto.getName()));
        }

        @Test
        @Description(value = "Тест на создание предмета с коротким названием")
        void Item_create_EmptyShortName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
           itemDto.setName("");
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название предмета должно состоять из 1-200 символов")));
        }

        @Test
        @Description(value = "Тест на создание предмета с длинным названием")
        void Item_create_LongName() throws Exception {
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
            itemDto.setName(name.toString());
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название предмета должно состоять из 1-200 символов")));
        }

        @Test
        @Description(value = "Тест на создание предмета без названия")
        void Item_create_NulltName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            itemDto.setName(null);
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название предмета должно состоять из 1-200 символов")));
        }

        @Test
        @Description(value = "Тест на создание предмета без названия")
        void Item_create_BlankName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            itemDto.setName("       ");
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название предмета должно состоять из 1-200 символов")));
        }

        @Test
        @Description(value = "Тест на создание предмета с отрицательным весом")
        void Item_create_NegativeWeight() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            itemDto.setWeight(-1L);
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Вес предмета должны быть равен или больше 0")));
        }

        @Test
        @Description(value = "Тест на создание предмета с ценностью меньше 1")
        void Item_create_Less1() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            itemDto.setValue(0);
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Ценность предмета должна быть больше 0")));
        }

        @Test
        @Description(value = "Тест на создание предмета с уникальным названием")
        void Item_create_UniqueName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            RequestParentTest.insertItem(mockMvc, objectMapper.writeValueAsString(itemDto), token);
            itemDto.setValue(0);
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Название предмета уже существует")));
        }

        @Test
        @Description(value = "Тест на создание предмета с недопустимым названием")
        void Item_create_NotAllowName() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            itemDto.setName("null");
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
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
        @Description(value = "Тест на создание предмета с несуществующим товаром")
        void Item_create_NotExistProduct() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.setAdminRights(mockMvc, token);
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isBadRequest())
                    .andExpect(
                            jsonPath("$.descr").value(containsString("Использован несуществующий товар")));
        }

        @Test
        @Description(value = "Тест на создание предмета без прав администратора")
        void Item_create_NoAdmin() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            String token = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertProduct(mockMvc, objectMapper.writeValueAsString(productDto), token);
            itemDto.setName("null");
            createRequest
                    .content(objectMapper.writeValueAsString(itemDto))
                    .header("Authorization", "Bearer " + token);
            //when
            mockMvc.perform(createRequest)
                    //then
                    .andExpect(
                            status().isForbidden());
        }
    }
}
