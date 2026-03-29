package com.mipt.sudarkingeorgiy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FavoritesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST + GET favorites поз")
    void addAndGetFavorites_positive() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/1").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/favorites/2").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("DELETE favorites поз")
    void removeFavorite_positive() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/1").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/favorites/1").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
