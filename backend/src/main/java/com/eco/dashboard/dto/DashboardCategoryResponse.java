package com.eco.dashboard.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class DashboardCategoryResponse {

    private final UUID categoryId;
    private final String categoryName;
    private final BigDecimal total;
    private final BigDecimal percentage;

    public DashboardCategoryResponse(UUID categoryId, String categoryName, BigDecimal total, BigDecimal percentage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.total = total;
        this.percentage = percentage;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }
}

