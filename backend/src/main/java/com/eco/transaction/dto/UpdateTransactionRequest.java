package com.eco.transaction.dto;

import com.eco.transaction.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class UpdateTransactionRequest {

    @NotBlank
    @Size(max = 120)
    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotNull
    private LocalDate occurredAt;

    @Pattern(regexp = "\\d{4}-\\d{2}")
    private String billingMonth;

    @NotNull
    private UUID accountId;

    @NotNull
    private UUID categoryId;

    @Size(max = 255)
    private String note;

    @NotNull
    private Boolean active;

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDate getOccurredAt() {
        return occurredAt;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getNote() {
        return note;
    }

    public Boolean getActive() {
        return active;
    }
}
