package com.eco.report.dto;

import java.math.BigDecimal;

public class MonthlySummaryResponse {
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;

    public MonthlySummaryResponse(BigDecimal income, BigDecimal expense, BigDecimal balance) {
        this.income = income;
        this.expense = expense;
        this.balance = balance;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
