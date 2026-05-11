package com.eco.dashboard.dto;

import java.math.BigDecimal;

public class DashboardBudgetResponse {

    private final BigDecimal generalLimit;
    private final BigDecimal spentAmount;
    private final BigDecimal usagePercent;

    public DashboardBudgetResponse(BigDecimal generalLimit, BigDecimal spentAmount, BigDecimal usagePercent) {
        this.generalLimit = generalLimit;
        this.spentAmount = spentAmount;
        this.usagePercent = usagePercent;
    }

    public BigDecimal getGeneralLimit() {
        return generalLimit;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public BigDecimal getUsagePercent() {
        return usagePercent;
    }
}

