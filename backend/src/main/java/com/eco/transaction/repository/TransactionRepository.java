package com.eco.transaction.repository;

import com.eco.transaction.model.Transaction;
import com.eco.transaction.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = :type
      and t.active = true
      and t.user.id = :userId
      and t.occurredAt between :startDate and :endDate
""")
    BigDecimal sumAmountByTypeAndPeriod(
            UUID userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = :type
      and t.active = true
      and t.user.id = :userId
      and t.account.id = :accountId
      and t.occurredAt between :startDate and :endDate
""")
    BigDecimal sumAmountByAccountAndTypeAndPeriod(
            UUID userId,
            UUID accountId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = com.eco.transaction.model.TransactionType.TRANSFER
      and t.active = true
      and t.user.id = :userId
      and t.transferAccount.id = :accountId
      and t.occurredAt between :startDate and :endDate
""")
    BigDecimal sumTransferInByAccountAndPeriod(
            UUID userId,
            UUID accountId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = com.eco.transaction.model.TransactionType.TRANSFER
      and t.active = true
      and t.user.id = :userId
      and t.account.id = :accountId
      and t.occurredAt between :startDate and :endDate
""")
    BigDecimal sumTransferOutByAccountAndPeriod(
            UUID userId,
            UUID accountId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = com.eco.transaction.model.TransactionType.EXPENSE
      and t.active = true
      and t.user.id = :userId
      and t.billingMonth = :billingMonth
""")
    BigDecimal sumCardExpensesByBillingMonth(UUID userId, String billingMonth);

    long countByUserIdAndTypeAndActiveTrueAndBillingMonth(UUID userId, TransactionType type, String billingMonth);

    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = com.eco.transaction.model.TransactionType.EXPENSE
      and t.active = true
      and t.user.id = :userId
      and t.account.type <> com.eco.account.model.AccountType.CREDIT_CARD
      and t.occurredAt between :startDate and :endDate
""")
    BigDecimal sumNonCardExpensesByPeriod(UUID userId, LocalDate startDate, LocalDate endDate);

    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = com.eco.transaction.model.TransactionType.EXPENSE
      and t.active = true
      and t.user.id = :userId
      and t.account.type <> com.eco.account.model.AccountType.CREDIT_CARD
      and t.category.id = :categoryId
      and t.occurredAt between :startDate and :endDate
""")
    BigDecimal sumNonCardExpensesByCategoryAndPeriod(UUID userId, UUID categoryId, LocalDate startDate, LocalDate endDate);

    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = com.eco.transaction.model.TransactionType.EXPENSE
      and t.active = true
      and t.user.id = :userId
      and t.account.type = com.eco.account.model.AccountType.CREDIT_CARD
      and t.billingMonth = :billingMonth
""")
    BigDecimal sumCardExpensesForBudget(UUID userId, String billingMonth);

    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = com.eco.transaction.model.TransactionType.EXPENSE
      and t.active = true
      and t.user.id = :userId
      and t.account.type = com.eco.account.model.AccountType.CREDIT_CARD
      and t.category.id = :categoryId
      and t.billingMonth = :billingMonth
""")
    BigDecimal sumCardExpensesByCategoryForBudget(UUID userId, UUID categoryId, String billingMonth);

    @Query("""
    select c.id, c.name, coalesce(sum(t.amount), 0)
    from Transaction t
    join t.category c
    where t.type = com.eco.transaction.model.TransactionType.EXPENSE
      and t.active = true
      and t.user.id = :userId
      and t.account.type <> com.eco.account.model.AccountType.CREDIT_CARD
      and t.occurredAt between :startDate and :endDate
    group by c.id, c.name
""")
    List<Object[]> sumNonCardExpensesGroupedByCategory(UUID userId, LocalDate startDate, LocalDate endDate);

    @Query("""
    select c.id, c.name, coalesce(sum(t.amount), 0)
    from Transaction t
    join t.category c
    where t.type = com.eco.transaction.model.TransactionType.EXPENSE
      and t.active = true
      and t.user.id = :userId
      and t.account.type = com.eco.account.model.AccountType.CREDIT_CARD
      and t.billingMonth = :billingMonth
    group by c.id, c.name
""")
    List<Object[]> sumCardExpensesGroupedByCategory(UUID userId, String billingMonth);
}
