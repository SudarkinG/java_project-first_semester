package com.mipt.sudarkingeorgiy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttachmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST upload поз")
    void uploadAttachment_positive_returns201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());

        mockMvc.perform(multipart("/api/tasks/10/attachments").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.size").value(11));
    }

    @Test
    @DisplayName("POST upload neg empty")
    void uploadAttachment_negative_emptyFile_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/tasks/10/attachments").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET download поз")
    void downloadAttachment_positive_returnsFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "download-test.txt", MediaType.TEXT_PLAIN_VALUE, "content".getBytes());

        MvcResult uploadResult = mockMvc.perform(multipart("/api/tasks/10/attachments").file(file))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        long attachmentId = json.get("id").asLong();

        mockMvc.perform(get("/api/attachments/" + attachmentId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=\"download-test.txt\""));
    }

    @Test
    @DisplayName("GET list поз")
    void getAttachments_positive_returnsList() throws Exception {
        mockMvc.perform(get("/api/tasks/10/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("DELETE attachment поз")
    void deleteAttachment_positive_returns204() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "to-delete.txt", MediaType.TEXT_PLAIN_VALUE, "delete me".getBytes());

        MvcResult uploadResult = mockMvc.perform(multipart("/api/tasks/10/attachments").file(file))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        long attachmentId = json.get("id").asLong();

        mockMvc.perform(delete("/api/attachments/" + attachmentId))
                .andExpect(status().isNoContent());
    }
}
