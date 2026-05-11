package com.eco.account.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountBalanceResponse {

    private UUID accountId;
    private BigDecimal initialBalance;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal transferIn;
    private BigDecimal transferOut;
    private BigDecimal balance;

    public AccountBalanceResponse(UUID accountId, BigDecimal initialBalance, BigDecimal income, BigDecimal expense,
                                  BigDecimal transferIn, BigDecimal transferOut, BigDecimal balance) {
        this.accountId = accountId;
        this.initialBalance = initialBalance;
        this.income = income;
        this.expense = expense;
        this.transferIn = transferIn;
        this.transferOut = transferOut;
        this.balance = balance;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public BigDecimal getTransferIn() {
        return transferIn;
    }

    public BigDecimal getTransferOut() {
        return transferOut;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
