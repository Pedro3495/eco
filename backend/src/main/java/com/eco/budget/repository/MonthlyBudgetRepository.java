package com.eco.budget.repository;

import com.eco.budget.model.MonthlyBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, UUID> {
    Optional<MonthlyBudget> findByUserIdAndMonth(UUID userId, String month);
}
