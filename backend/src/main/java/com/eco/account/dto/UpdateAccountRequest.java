package com.eco.account.dto;

import com.eco.account.model.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateAccountRequest {
    @NotBlank
    @Size(max = 80)
    private String name;

    @NotNull
    private AccountType type;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal initialBalance;

    @NotNull
    private Boolean active;

    public String getName() {
        return name;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public Boolean getActive() {
        return active;
    }
}
