package com.eco.budget.model;

import com.eco.category.model.Category;
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
@Table(name = "category_budgets")
public class CategoryBudget {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_budget_id", nullable = false)
    private MonthlyBudget monthlyBudget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "limit_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CategoryBudget() {
    }

    public CategoryBudget(MonthlyBudget monthlyBudget, Category category, BigDecimal limitAmount) {
        this.id = UUID.randomUUID();
        this.monthlyBudget = monthlyBudget;
        this.category = category;
        this.limitAmount = limitAmount;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public MonthlyBudget getMonthlyBudget() {
        return monthlyBudget;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
