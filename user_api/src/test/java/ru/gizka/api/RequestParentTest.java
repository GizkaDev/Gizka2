package ru.gizka.api;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RequestParentTest {
    public static void requestWithTokenCheckForbidden(String token, MockHttpServletRequestBuilder requestBuilder, MockMvc mockMvc) throws Exception {
        //given
        requestBuilder.header("Authorization", "Bearer " + token);
        //when
        mockMvc.perform(requestBuilder)
                //then
                .andExpect(
                        status().isForbidden());
    }
}
