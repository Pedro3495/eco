package com.eco.budget.dto;

import com.eco.budget.model.CategoryBudget;

import java.math.BigDecimal;
import java.util.UUID;

public class CategoryBudgetResponse {

    private UUID categoryId;
    private String categoryName;
    private BigDecimal limitAmount;

    public CategoryBudgetResponse(UUID categoryId, String categoryName, BigDecimal limitAmount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.limitAmount = limitAmount;
    }

    public static CategoryBudgetResponse fromEntity(CategoryBudget categoryBudget) {
        return new CategoryBudgetResponse(
                categoryBudget.getCategory().getId(),
                categoryBudget.getCategory().getName(),
                categoryBudget.getLimitAmount()
        );
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }
}
