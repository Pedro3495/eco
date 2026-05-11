package com.eco.dashboard.dto;

import java.math.BigDecimal;

public class DashboardCashFlowResponse {

    private final String month;
    private final BigDecimal income;
    private final BigDecimal expense;
    private final BigDecimal result;

    public DashboardCashFlowResponse(String month, BigDecimal income, BigDecimal expense, BigDecimal result) {
        this.month = month;
        this.income = income;
        this.expense = expense;
        this.result = result;
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
}

