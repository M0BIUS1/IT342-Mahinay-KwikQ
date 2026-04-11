package edu.cit.mahinay.kwikq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerRoleFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerWithoutRoleDefaultsToStudent() throws Exception {
        String email = uniqueEmail();

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Student User");
        payload.put("email", email);
        payload.put("password", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void registerWithRoleAdminPersistsAdminRole() throws Exception {
        String email = uniqueEmail();

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Admin User");
        payload.put("email", email);
        payload.put("password", "password123");
        payload.put("role", "ADMIN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void registerWithInvalidRoleReturnsBadRequest() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Invalid Role User");
        payload.put("email", uniqueEmail());
        payload.put("password", "password123");
        payload.put("role", "TEACHER");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Role must be one of")));
    }

    @Test
    void loginReturnsAssignedLibrarianRole() throws Exception {
        String email = uniqueEmail();
        String password = "password123";

        Map<String, Object> registerPayload = new HashMap<>();
        registerPayload.put("name", "Librarian User");
        registerPayload.put("email", email);
        registerPayload.put("password", password);
        registerPayload.put("role", "LIBRARIAN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated());

        Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", email);
        loginPayload.put("password", password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("LIBRARIAN"));
    }

    private String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}
