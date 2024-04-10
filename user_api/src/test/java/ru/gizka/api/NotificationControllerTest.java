package ru.gizka.api;

import com.fasterxml.jackson.core.type.TypeReference;
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
import ru.gizka.api.dto.notification.NotificationDto;
import ru.gizka.api.dto.hero.RequestHeroDto;
import ru.gizka.api.dto.user.RequestAppUserDto;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class NotificationControllerTest extends RequestParentTest {
    private String uri = "/api/user/event";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private RequestAppUserDto userDto;
    private RequestAppUserDto userDto2;
    private RequestHeroDto heroDto;
    private RequestHeroDto heroDto2;

    @Autowired
    private NotificationControllerTest(MockMvc mockMvc,
                                       ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        userDto = RequestAppUserDto.builder()
                .login("Biba")
                .password("Qwerty12345!")
                .build();

        userDto2 = RequestAppUserDto.builder()
                .login("Boba")
                .password("Qwerty12345!")
                .build();

        heroDto = RequestHeroDto.builder()
                .name("Gizka")
                .lastName("Green")
                .str(10)
                .dex(8)
                .con(12)
//                .race(Race.ELF.name())
                .build();

        heroDto2 = RequestHeroDto.builder()
                .name("Lyakusha")
                .lastName("Swamp")
                .str(10)
                .dex(12)
                .con(8)
//                .race(Race.HUMAN.name())
                .build();
    }

    @Nested
    @DisplayName(value = "Тесты на получение оповещений")
    class GetAllSortedByDateTest{

        @Test
        @Description(value = "Тест на получение оповещений в сортированном виде")
        void Event_GetAllSortedByDate_Success() throws Exception {
            //given
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertUser(mockMvc, objectMapper.writeValueAsString(userDto2));
            String token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            String token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto), token1);
            RequestParentTest.insertHero(mockMvc, objectMapper.writeValueAsString(heroDto2), token2);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertDuel(mockMvc, userDto2.getLogin(), token1);

            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token2);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            RequestParentTest.insertDuel(mockMvc, userDto2.getLogin(), token1);

            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            RequestParentTest.insertDuel(mockMvc, userDto.getLogin(), token2);

            token1 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto));
            token2 = RequestParentTest.getTokenRequest(mockMvc, objectMapper.writeValueAsString(userDto2));
            MockHttpServletRequestBuilder eventRequest1 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token1));

            MockHttpServletRequestBuilder eventRequest2 = MockMvcRequestBuilders
                    .get(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", String.format("Bearer %s", token2));

            //when
            String response1 = mockMvc.perform(eventRequest1)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$", hasSize(4)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String response2 = mockMvc.perform(eventRequest2)
                    //then
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            jsonPath("$", hasSize(4)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<NotificationDto> events1 = objectMapper.readValue(response1, new TypeReference<>() {
            });
            List<NotificationDto> events2 = objectMapper.readValue(response2, new TypeReference<>() {
            });

            for (int i = 0; i < events1.size() - 1; i++) {
                Date createdAt1E1 = events1.get(i).getCreatedAt();
                Date createdAt2E1 = events1.get(i + 1).getCreatedAt();
                assertTrue(createdAt1E1.after(createdAt2E1) || createdAt1E1.equals(createdAt2E1));

                Date createdAt1E2 = events2.get(i).getCreatedAt();
                Date createdAt2E2 = events2.get(i + 1).getCreatedAt();
                assertTrue(createdAt1E2.after(createdAt2E1) || createdAt1E2.equals(createdAt2E2));
            }
        }
    }
}
