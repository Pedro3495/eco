package com.eco.goal.model;

import com.eco.common.exception.BusinessException;
import com.eco.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "target_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentAmount;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Goal() {
    }

    public Goal(String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate targetDate, User user) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.status = resolveStatus(currentAmount, targetAmount);
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
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

    public void update(String name, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate targetDate, GoalStatus status) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.status = status;
        touch();
    }

    public void updateProgress(BigDecimal currentAmount) {
        if (status == GoalStatus.ARCHIVED) {
            throw new BusinessException("Meta arquivada nao pode receber progresso");
        }

        this.currentAmount = currentAmount;
        this.status = resolveStatus(currentAmount, targetAmount);
        touch();
    }

    public void archive() {
        this.status = GoalStatus.ARCHIVED;
        touch();
    }

    private GoalStatus resolveStatus(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (currentAmount.compareTo(targetAmount) >= 0) {
            return GoalStatus.COMPLETED;
        }

        return GoalStatus.ACTIVE;
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}

