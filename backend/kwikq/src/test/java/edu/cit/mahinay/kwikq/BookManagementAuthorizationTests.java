package edu.cit.mahinay.kwikq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookManagementAuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAndDeleteBook() throws Exception {
        Long id = createBookAndReturnId();

        mockMvc.perform(delete("/api/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted"));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void librarianCanCreateAndUpdateBook() throws Exception {
        Long id = createBookAndReturnId();

        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("title", "Updated Title");
        updatePayload.put("author", "Updated Author");
        updatePayload.put("category", "Updated Category");
        updatePayload.put("uniqueCode", "UPDATED-CODE-001");

        mockMvc.perform(put("/api/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.uniqueCode").value("UPDATED-CODE-001"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotManageBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookPayload("STUDENT-CODE-1"))))
                .andExpect(status().isForbidden());
    }

            @Test
            @WithMockUser(roles = "ADMIN")
            void adminCanQueryAndPaginateBooks() throws Exception {
            createBookAndReturnId();
            mockMvc.perform(post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validBookPayload("BOOK-SEARCH-0002"))))
                .andExpect(status().isCreated());

            mockMvc.perform(get("/api/books")
                    .param("query", "sample")
                    .param("page", "0")
                    .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
            }

    private Long createBookAndReturnId() throws Exception {
        String body = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookPayload("BOOK-" + System.nanoTime()))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body).get("id").asLong();
    }

    private Map<String, Object> validBookPayload(String uniqueCode) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "Sample Book");
        payload.put("author", "Sample Author");
        payload.put("category", "Sample Category");
        payload.put("uniqueCode", uniqueCode);
        return payload;
    }
}
