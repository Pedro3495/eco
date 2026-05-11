package com.eco.budget.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CategoryBudgetSummaryResponse {

    private UUID categoryId;
    private String categoryName;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private BigDecimal usagePercent;

    public CategoryBudgetSummaryResponse(UUID categoryId, String categoryName, BigDecimal limitAmount,
                                         BigDecimal spentAmount, BigDecimal usagePercent) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
        this.usagePercent = usagePercent;
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

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public BigDecimal getUsagePercent() {
        return usagePercent;
    }
}
