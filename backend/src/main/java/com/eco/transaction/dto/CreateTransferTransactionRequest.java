package com.eco.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateTransferTransactionRequest {

    @NotNull
    private UUID fromAccountId;

    @NotNull
    private UUID toAccountId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private LocalDate occurredAt;

    @NotBlank
    @Size(max = 120)
    private String description;

    @Size(max = 255)
    private String note;

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getOccurredAt() {
        return occurredAt;
    }

    public String getDescription() {
        return description;
    }

    public String getNote() {
        return note;
    }
}
