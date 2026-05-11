package com.eco.budget.model;

import com.eco.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "monthly_budgets")
public class MonthlyBudget {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 7)
    private String month;

    @Column(name = "general_limit", precision = 15, scale = 2)
    private BigDecimal generalLimit;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected MonthlyBudget() {
    }

    public MonthlyBudget(User user, String month, BigDecimal generalLimit) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.month = month;
        this.generalLimit = generalLimit;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getGeneralLimit() {
        return generalLimit;
    }

    public void setGeneralLimit(BigDecimal generalLimit) {
        this.generalLimit = generalLimit;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
