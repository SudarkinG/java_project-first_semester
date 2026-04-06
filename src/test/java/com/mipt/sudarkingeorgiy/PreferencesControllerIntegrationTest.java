package com.mipt.sudarkingeorgiy;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PreferencesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST preference поз")
    void setViewPreference_positive_setsCookie() throws Exception {
        mockMvc.perform(post("/api/preferences/view").param("mode", "detailed"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("viewPreference=detailed")))
                .andExpect(jsonPath("$.viewPreference").value("detailed"));
    }

    @Test
    @DisplayName("GET preference с кукой")
    void getViewPreference_positive_readsCookie() throws Exception {
        mockMvc.perform(get("/api/preferences/view")
                        .cookie(new Cookie("viewPreference", "detailed")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewPreference").value("detailed"));
    }
}
