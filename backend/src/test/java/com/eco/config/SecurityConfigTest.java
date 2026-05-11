package com.eco.config;

import com.eco.auth.service.JwtService;
import com.eco.report.controller.ReportController;
import com.eco.report.service.ReportService;
import com.eco.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void protectedEndpointShouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/reports/monthly-summary")
                        .param("year", "2026")
                        .param("month", "5"))
                .andExpect(status().isUnauthorized());
    }
}
