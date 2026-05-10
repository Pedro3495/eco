package com.eco.transaction.repository;

import com.eco.transaction.model.Transaction;
import com.eco.transaction.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
    @Query("""
    select coalesce(sum(t.amount), 0)
    from Transaction t
    where t.type = :type
      and t.active = true
      and t.occurredAt between :startDate and :endDate
""")
    BigDecimal sumAmountByTypeAndPeriod(
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate
    );
}
