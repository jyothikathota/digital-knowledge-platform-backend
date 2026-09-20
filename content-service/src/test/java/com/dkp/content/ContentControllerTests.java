package com.dkp.content;

import com.dkp.content.dto.ContentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testContentCrudWorkflow() throws Exception {
        ContentRequest createRequest = new ContentRequest(
                "Java Programming",
                "Deepak",
                "BOOK",
                "PROGRAMMING",
                "Java programming learning resource",
                "https://example.com/java"
        );

        // 1. POST /api/content
        String responseContent = mockMvc.perform(post("/api/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Java Programming"))
                .andExpect(jsonPath("$.author").value("Deepak"))
                .andReturn().getResponse().getContentAsString();

        Long contentId = objectMapper.readTree(responseContent).get("id").asLong();

        // 2. GET /api/content
        mockMvc.perform(get("/api/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 3. GET /api/content/{id}
        mockMvc.perform(get("/api/content/" + contentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Programming"));

        // 4. GET /api/content/search?keyword=java
        mockMvc.perform(get("/api/content/search?keyword=java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Programming"));

        // 5. PUT /api/content/{id}
        ContentRequest updateRequest = new ContentRequest(
                "Java Programming Masterclass",
                "Deepak",
                "BOOK",
                "PROGRAMMING",
                "Updated edition",
                "https://example.com/java-updated"
        );

        mockMvc.perform(put("/api/content/" + contentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Programming Masterclass"));

        // 6. DELETE /api/content/{id}
        mockMvc.perform(delete("/api/content/" + contentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Content deleted successfully"));

        // 7. GET after delete should return 404
        mockMvc.perform(get("/api/content/" + contentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateContentValidationFailure() throws Exception {
        // Missing title and author
        ContentRequest invalidRequest = new ContentRequest(
                "",
                "",
                "BOOK",
                "PROGRAMMING",
                "Missing details",
                "https://example.com/fail"
        );

        mockMvc.perform(post("/api/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
