package com.eco.dashboard.controller;

import com.eco.dashboard.dto.DashboardCashFlowResponse;
import com.eco.dashboard.dto.DashboardCategoryResponse;
import com.eco.dashboard.dto.DashboardMonthlyResponse;
import com.eco.dashboard.service.DashboardService;
import com.eco.user.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/monthly")
    public DashboardMonthlyResponse getMonthly(@RequestParam String month, @AuthenticationPrincipal User user) {
        return dashboardService.getMonthly(month, user);
    }

    @GetMapping("/categories")
    public List<DashboardCategoryResponse> getCategories(@RequestParam String month, @AuthenticationPrincipal User user) {
        return dashboardService.getCategories(month, user);
    }

    @GetMapping("/cash-flow")
    public List<DashboardCashFlowResponse> getCashFlow(
            @RequestParam String from,
            @RequestParam String to,
            @AuthenticationPrincipal User user
    ) {
        return dashboardService.getCashFlow(from, to, user);
    }
}

