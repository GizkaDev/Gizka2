package ru.gizka.api;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RequestParentTest {
    public static void insertUser(MockMvc mockMvc, String userDtoAsString) throws Exception {
        RequestBuilder userRequest =
                MockMvcRequestBuilders
                        .post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoAsString);
        mockMvc.perform(userRequest);
    }

    public static String getTokenRequest(MockMvc mockMvc, String userDtoAsString) throws Exception {
        RequestBuilder tokenRequest = MockMvcRequestBuilders
                .post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDtoAsString);

        return mockMvc.perform(tokenRequest)
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    public static void insertHero(MockMvc mockMvc, String heroDtoAsString, String token) throws Exception {
        RequestBuilder heroRequest = MockMvcRequestBuilders
                .post("/api/user/hero")
                .contentType(MediaType.APPLICATION_JSON)
                .content(heroDtoAsString)
                .header("Authorization", String.format("Bearer %s", token));
        mockMvc.perform(heroRequest);
    }

    public static void insertDuel(MockMvc mockMvc, String opponentLogin, String token) throws Exception {
        RequestBuilder duelRequest = MockMvcRequestBuilders
                .post(String.format("%s?login=%s", "/api/user/hero/duel", opponentLogin))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", token));
        mockMvc.perform(duelRequest);
    }

    public static ResultActions getCurrentHero(MockMvc mockMvc, String token) throws Exception {
        RequestBuilder getCurrentHeroRequest = MockMvcRequestBuilders
                .get("/api/user/hero")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", token));
        return mockMvc.perform(getCurrentHeroRequest);
    }

    public static ResultActions getEventsSortedByDate(MockMvc mockMvc, String token) throws Exception {
        MockHttpServletRequestBuilder eventRequest = MockMvcRequestBuilders
                .get("/api/user/event")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", token));
        return mockMvc.perform(eventRequest);
    }

    public static ResultActions setAdminRights(MockMvc mockMvc, String token) throws Exception {
        MockHttpServletRequestBuilder adminRequest = MockMvcRequestBuilders
                .put("/api/user/own")
                .contentType(MediaType.APPLICATION_JSON)
                .content("SECRET")
                .header("Authorization", "Bearer " + token);
        return mockMvc.perform(adminRequest);
    }

    public static ResultActions insertRace(MockMvc mockMvc, String token, String raceDtoAsString) throws Exception {
        MockHttpServletRequestBuilder raceRequest = MockMvcRequestBuilders
                .post("/api/admin/race")
                .contentType(MediaType.APPLICATION_JSON)
                .content(raceDtoAsString)
                .header("Authorization", "Bearer " + token);
        return mockMvc.perform(raceRequest);
    }

    public static ResultActions insertCreature(MockMvc mockMvc, String token, String creatureDtoAsString) throws Exception {
        MockHttpServletRequestBuilder creatureRequest = MockMvcRequestBuilders
                .post("/api/admin/creature")
                .contentType(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(creatureDtoAsString)
                .header("Authorization", "Bearer " + token);
        return mockMvc.perform(creatureRequest);
    }

    public static ResultActions getCreature(MockMvc mockMvc, String token, String name) throws Exception {
        RequestBuilder getCurrentHeroRequest = MockMvcRequestBuilders
                .get(String.format("/api/creature/%s", name))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", token));
        return mockMvc.perform(getCurrentHeroRequest);
    }

    public static void insertFight(MockMvc mockMvc, String name, String token) throws Exception {
        RequestBuilder fightRequest = MockMvcRequestBuilders
                .post(String.format("%s?name=%s", "/api/user/hero/fight", name))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", token));
        mockMvc.perform(fightRequest);
    }
}
