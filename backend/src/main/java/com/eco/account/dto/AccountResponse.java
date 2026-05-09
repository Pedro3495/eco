package com.eco.account.dto;

import com.eco.account.model.Account;
import com.eco.account.model.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountResponse {
    private UUID id;
    private String name;
    private AccountType type;
    private BigDecimal initialBalance;
    private Boolean active;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AccountResponse(UUID id, String name, AccountType type, BigDecimal initialBalance, Boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.initialBalance = initialBalance;
        this.active = active;
    }
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getInitialBalance(),
                account.isActive()
        );
    }
}
