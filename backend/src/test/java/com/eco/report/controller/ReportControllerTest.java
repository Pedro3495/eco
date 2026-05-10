package com.eco.report.controller;

import com.eco.report.dto.MonthlySummaryResponse;
import com.eco.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    void getMonthlySummaryShouldReturnSummary() throws Exception {
        MonthlySummaryResponse response = new MonthlySummaryResponse(
                new BigDecimal("5000.00"),
                new BigDecimal("2300.00"),
                new BigDecimal("2700.00")
        );

        when(reportService.getMonthlySummary(2026, 5)).thenReturn(response);

        mockMvc.perform(get("/reports/monthly-summary")
                        .param("year", "2026")
                        .param("month", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(5000.00))
                .andExpect(jsonPath("$.expense").value(2300.00))
                .andExpect(jsonPath("$.balance").value(2700.00));
    }

}
