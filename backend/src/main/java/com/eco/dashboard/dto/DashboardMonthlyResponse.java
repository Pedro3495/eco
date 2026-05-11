package com.eco.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardMonthlyResponse {

    private final String month;
    private final BigDecimal income;
    private final BigDecimal expense;
    private final BigDecimal result;
    private final BigDecimal creditCardTotal;
    private final DashboardBudgetResponse budget;
    private final List<DashboardGoalResponse> goals;

    public DashboardMonthlyResponse(
            String month,
            BigDecimal income,
            BigDecimal expense,
            BigDecimal result,
            BigDecimal creditCardTotal,
            DashboardBudgetResponse budget,
            List<DashboardGoalResponse> goals
    ) {
        this.month = month;
        this.income = income;
        this.expense = expense;
        this.result = result;
        this.creditCardTotal = creditCardTotal;
        this.budget = budget;
        this.goals = goals;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public BigDecimal getResult() {
        return result;
    }

    public BigDecimal getCreditCardTotal() {
        return creditCardTotal;
    }

    public DashboardBudgetResponse getBudget() {
        return budget;
    }

    public List<DashboardGoalResponse> getGoals() {
        return goals;
    }
}

