package com.eco.transaction.model;

import com.eco.account.model.Account;
import com.eco.category.model.Category;
import com.eco.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "occurred_at", nullable = false)
    private LocalDate occurredAt;

    @Column(name = "billing_month", length = 7)
    private String billingMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_account_id")
    private Account transferAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 255)
    private String note;

    @Column(name = "installment_group_id")
    private UUID installmentGroupId;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    @Column(name = "installment_total")
    private Integer installmentTotal;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Transaction() {
    }

    public Transaction(String description, BigDecimal amount, TransactionType type, LocalDate occurredAt,
                       Account account, Category category, User user, String note) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.occurredAt = occurredAt;
        this.account = account;
        this.category = category;
        this.user = user;
        this.note = note;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static Transaction transfer(String description, BigDecimal amount, LocalDate occurredAt,
                                       Account fromAccount, Account toAccount, User user, String note) {
        Transaction transaction = new Transaction(
                description,
                amount,
                TransactionType.TRANSFER,
                occurredAt,
                fromAccount,
                null,
                user,
                note
        );
        transaction.setTransferAccount(toAccount);
        return transaction;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDate getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDate occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Account getTransferAccount() {
        return transferAccount;
    }

    public void setTransferAccount(Account transferAccount) {
        this.transferAccount = transferAccount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UUID getInstallmentGroupId() {
        return installmentGroupId;
    }

    public void setInstallmentGroupId(UUID installmentGroupId) {
        this.installmentGroupId = installmentGroupId;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public Integer getInstallmentTotal() {
        return installmentTotal;
    }

    public void setInstallmentTotal(Integer installmentTotal) {
        this.installmentTotal = installmentTotal;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
