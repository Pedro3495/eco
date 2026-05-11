package com.eco.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateInstallmentTransactionRequest {

    @NotNull
    private UUID accountId;

    @NotNull
    private UUID categoryId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal totalAmount;

    @NotNull
    @Min(2)
    private Integer installmentTotal;

    @NotNull
    private LocalDate firstOccurredAt;

    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}")
    private String firstBillingMonth;

    @NotBlank
    @Size(max = 120)
    private String description;

    @Size(max = 255)
    private String note;

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Integer getInstallmentTotal() {
        return installmentTotal;
    }

    public LocalDate getFirstOccurredAt() {
        return firstOccurredAt;
    }

    public String getFirstBillingMonth() {
        return firstBillingMonth;
    }

    public String getDescription() {
        return description;
    }

    public String getNote() {
        return note;
    }
}
