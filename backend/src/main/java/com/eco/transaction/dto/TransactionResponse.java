package com.eco.transaction.dto;

import com.eco.transaction.model.Transaction;
import com.eco.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionResponse {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate occurredAt;
    private String billingMonth;
    private UUID accountId;
    private String accountName;
    private UUID categoryId;
    private String categoryName;
    private UUID transferAccountId;
    private String transferAccountName;
    private String note;
    private UUID installmentGroupId;
    private Integer installmentNumber;
    private Integer installmentTotal;
    private Boolean active;

    public TransactionResponse(UUID id, String description, BigDecimal amount, TransactionType type,
                               LocalDate occurredAt, String billingMonth, UUID accountId, String accountName, UUID categoryId,
                               String categoryName, UUID transferAccountId, String transferAccountName,
                               String note, UUID installmentGroupId, Integer installmentNumber,
                               Integer installmentTotal, Boolean active) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.occurredAt = occurredAt;
        this.billingMonth = billingMonth;
        this.accountId = accountId;
        this.accountName = accountName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.transferAccountId = transferAccountId;
        this.transferAccountName = transferAccountName;
        this.note = note;
        this.installmentGroupId = installmentGroupId;
        this.installmentNumber = installmentNumber;
        this.installmentTotal = installmentTotal;
        this.active = active;
    }

    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getOccurredAt(),
                transaction.getBillingMonth(),
                transaction.getAccount().getId(),
                transaction.getAccount().getName(),
                transaction.getCategory() == null ? null : transaction.getCategory().getId(),
                transaction.getCategory() == null ? null : transaction.getCategory().getName(),
                transaction.getTransferAccount() == null ? null : transaction.getTransferAccount().getId(),
                transaction.getTransferAccount() == null ? null : transaction.getTransferAccount().getName(),
                transaction.getNote(),
                transaction.getInstallmentGroupId(),
                transaction.getInstallmentNumber(),
                transaction.getInstallmentTotal(),
                transaction.isActive()
        );
    }

    public UUID getId() {
        return id;
    }

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

    public String getAccountName() {
        return accountName;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public UUID getTransferAccountId() {
        return transferAccountId;
    }

    public String getTransferAccountName() {
        return transferAccountName;
    }

    public String getNote() {
        return note;
    }

    public UUID getInstallmentGroupId() {
        return installmentGroupId;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public Integer getInstallmentTotal() {
        return installmentTotal;
    }

    public Boolean getActive() {
        return active;
    }
}
