package com.eco.budget.dto;

import java.math.BigDecimal;
import java.util.List;

public class BudgetSummaryResponse {

    private String month;
    private BigDecimal generalLimit;
    private BigDecimal totalSpent;
    private BigDecimal generalUsagePercent;
    private List<CategoryBudgetSummaryResponse> categories;

    public BudgetSummaryResponse(String month, BigDecimal generalLimit, BigDecimal totalSpent,
                                 BigDecimal generalUsagePercent, List<CategoryBudgetSummaryResponse> categories) {
        this.month = month;
        this.generalLimit = generalLimit;
        this.totalSpent = totalSpent;
        this.generalUsagePercent = generalUsagePercent;
        this.categories = categories;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getGeneralLimit() {
        return generalLimit;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public BigDecimal getGeneralUsagePercent() {
        return generalUsagePercent;
    }

    public List<CategoryBudgetSummaryResponse> getCategories() {
        return categories;
    }
}
