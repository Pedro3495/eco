package com.eco.goal.dto;

import com.eco.goal.model.Goal;
import com.eco.goal.model.GoalStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

public class GoalResponse {

    private final UUID id;
    private final String name;
    private final BigDecimal targetAmount;
    private final BigDecimal currentAmount;
    private final LocalDate targetDate;
    private final GoalStatus status;
    private final BigDecimal progressPercent;

    public GoalResponse(
            UUID id,
            String name,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            LocalDate targetDate,
            GoalStatus status,
            BigDecimal progressPercent
    ) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.status = status;
        this.progressPercent = progressPercent;
    }

    public static GoalResponse fromEntity(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getTargetDate(),
                goal.getStatus(),
                progress(goal.getCurrentAmount(), goal.getTargetAmount())
        );
    }

    public UUID getId() {
        return id;
    }

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

    public BigDecimal getProgressPercent() {
        return progressPercent;
    }

    private static BigDecimal progress(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (targetAmount == null || BigDecimal.ZERO.compareTo(targetAmount) == 0) {
            return BigDecimal.ZERO;
        }

        return currentAmount.multiply(new BigDecimal("100")).divide(targetAmount, 2, RoundingMode.HALF_UP);
    }
}

