package edu.cit.mahinay.kwikq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoleDashboardAuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedUserCannotAccessProtectedDashboardEndpoints() throws Exception {
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/api/dashboard/librarian"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/api/dashboard/student"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessAdminAndLibrarianEndpoints() throws Exception {
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/librarian"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dashboard/student"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCanAccessStudentEndpointOnly() throws Exception {
        mockMvc.perform(get("/api/dashboard/admin"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/dashboard/librarian"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/dashboard/student"))
                .andExpect(status().isOk());
    }
}
