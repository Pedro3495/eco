package com.eco.budget.repository;

import com.eco.budget.model.CategoryBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, UUID> {
    List<CategoryBudget> findAllByMonthlyBudgetId(UUID monthlyBudgetId);
    Optional<CategoryBudget> findByMonthlyBudgetIdAndCategoryId(UUID monthlyBudgetId, UUID categoryId);
}
