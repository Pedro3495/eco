package com.eco.budget.dto;

import com.eco.budget.model.CategoryBudget;
import com.eco.budget.model.MonthlyBudget;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MonthlyBudgetResponse {

    private UUID id;
    private String month;
    private BigDecimal generalLimit;
    private List<CategoryBudgetResponse> categories;

    public MonthlyBudgetResponse(UUID id, String month, BigDecimal generalLimit, List<CategoryBudgetResponse> categories) {
        this.id = id;
        this.month = month;
        this.generalLimit = generalLimit;
        this.categories = categories;
    }

    public static MonthlyBudgetResponse fromEntity(MonthlyBudget monthlyBudget, List<CategoryBudget> categoryBudgets) {
        return new MonthlyBudgetResponse(
                monthlyBudget.getId(),
                monthlyBudget.getMonth(),
                monthlyBudget.getGeneralLimit(),
                categoryBudgets.stream().map(CategoryBudgetResponse::fromEntity).toList()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getGeneralLimit() {
        return generalLimit;
    }

    public List<CategoryBudgetResponse> getCategories() {
        return categories;
    }
}
