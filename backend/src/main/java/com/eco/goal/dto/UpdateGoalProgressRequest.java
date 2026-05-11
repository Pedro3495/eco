package com.eco.goal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class UpdateGoalProgressRequest {

    @NotNull
    @PositiveOrZero
    private BigDecimal currentAmount;

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }
}

