package org.example.eshop.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminSecurityMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /admin/login should be accessible without auth and include CSRF hidden field")
    void getAdminLogin_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("type=\"hidden\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    @DisplayName("POST /admin/login with CSRF and valid creds should redirect to /admin/dashboard")
    void postAdminLogin_withCsrf_shouldRedirect() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .param("username", "admin")
                        .param("password", "admin123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("POST /admin/login without CSRF should be forbidden")
    void postAdminLogin_withoutCsrf_shouldBeForbidden() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().isForbidden());
    }
}
