package com.eco.budget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateCategoryBudgetRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal limitAmount;

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }
}
