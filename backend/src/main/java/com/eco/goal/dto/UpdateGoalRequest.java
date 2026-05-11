package com.eco.goal.dto;

import com.eco.goal.model.GoalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateGoalRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    @Positive
    private BigDecimal targetAmount;

    @NotNull
    @PositiveOrZero
    private BigDecimal currentAmount;

    private LocalDate targetDate;

    @NotNull
    private GoalStatus status;

    public String getName() {
        return name;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public GoalStatus getStatus() {
        return status;
    }
}

