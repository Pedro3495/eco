package com.eco.budget.controller;

import com.eco.budget.dto.BudgetSummaryResponse;
import com.eco.budget.dto.CategoryBudgetResponse;
import com.eco.budget.dto.MonthlyBudgetResponse;
import com.eco.budget.dto.UpdateCategoryBudgetRequest;
import com.eco.budget.dto.UpdateMonthlyBudgetRequest;
import com.eco.budget.service.BudgetService;
import com.eco.user.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/{month}")
    public MonthlyBudgetResponse findByMonth(@PathVariable String month, @AuthenticationPrincipal User user) {
        return budgetService.findByMonth(month, user);
    }

    @PutMapping("/{month}")
    public MonthlyBudgetResponse upsertMonthlyBudget(
            @PathVariable String month,
            @RequestBody @Valid UpdateMonthlyBudgetRequest request,
            @AuthenticationPrincipal User user
    ) {
        return budgetService.upsertMonthlyBudget(month, request, user);
    }

    @PutMapping("/{month}/categories/{categoryId}")
    public CategoryBudgetResponse upsertCategoryBudget(
            @PathVariable String month,
            @PathVariable UUID categoryId,
            @RequestBody @Valid UpdateCategoryBudgetRequest request,
            @AuthenticationPrincipal User user
    ) {
        return budgetService.upsertCategoryBudget(month, categoryId, request, user);
    }

    @DeleteMapping("/{month}/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryBudget(
            @PathVariable String month,
            @PathVariable UUID categoryId,
            @AuthenticationPrincipal User user
    ) {
        budgetService.deleteCategoryBudget(month, categoryId, user);
    }

    @GetMapping("/{month}/summary")
    public BudgetSummaryResponse getSummary(@PathVariable String month, @AuthenticationPrincipal User user) {
        return budgetService.getSummary(month, user);
    }
}
