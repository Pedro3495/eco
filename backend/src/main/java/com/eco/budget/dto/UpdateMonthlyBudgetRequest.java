package com.eco.budget.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class UpdateMonthlyBudgetRequest {

    @DecimalMin("0.00")
    private BigDecimal generalLimit;

    public BigDecimal getGeneralLimit() {
        return generalLimit;
    }
}
