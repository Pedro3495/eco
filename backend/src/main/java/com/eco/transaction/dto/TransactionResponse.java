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
    private UUID accountId;
    private String accountName;
    private UUID categoryId;
    private String categoryName;
    private String note;
    private Boolean active;

    public TransactionResponse(UUID id, String description, BigDecimal amount, TransactionType type,
                               LocalDate occurredAt, UUID accountId, String accountName, UUID categoryId,
                               String categoryName, String note, Boolean active) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.occurredAt = occurredAt;
        this.accountId = accountId;
        this.accountName = accountName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.note = note;
        this.active = active;
    }

    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getOccurredAt(),
                transaction.getAccount().getId(),
                transaction.getAccount().getName(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getNote(),
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

    public String getNote() {
        return note;
    }

    public Boolean getActive() {
        return active;
    }
}
