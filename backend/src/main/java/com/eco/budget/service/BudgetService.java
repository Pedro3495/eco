package com.eco.budget.service;

import com.eco.budget.dto.BudgetSummaryResponse;
import com.eco.budget.dto.CategoryBudgetResponse;
import com.eco.budget.dto.CategoryBudgetSummaryResponse;
import com.eco.budget.dto.MonthlyBudgetResponse;
import com.eco.budget.dto.UpdateCategoryBudgetRequest;
import com.eco.budget.dto.UpdateMonthlyBudgetRequest;
import com.eco.budget.model.CategoryBudget;
import com.eco.budget.model.MonthlyBudget;
import com.eco.budget.repository.CategoryBudgetRepository;
import com.eco.budget.repository.MonthlyBudgetRepository;
import com.eco.category.model.Category;
import com.eco.category.repository.CategoryRepository;
import com.eco.common.exception.NotFoundException;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class BudgetService {

    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final CategoryBudgetRepository categoryBudgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(MonthlyBudgetRepository monthlyBudgetRepository,
                         CategoryBudgetRepository categoryBudgetRepository,
                         CategoryRepository categoryRepository,
                         TransactionRepository transactionRepository) {
        this.monthlyBudgetRepository = monthlyBudgetRepository;
        this.categoryBudgetRepository = categoryBudgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public MonthlyBudgetResponse findByMonth(String month, User user) {
        MonthlyBudget monthlyBudget = findMonthlyBudget(month, user);
        List<CategoryBudget> categoryBudgets = categoryBudgetRepository.findAllByMonthlyBudgetId(monthlyBudget.getId());

        return MonthlyBudgetResponse.fromEntity(monthlyBudget, categoryBudgets);
    }

    @Transactional
    public MonthlyBudgetResponse upsertMonthlyBudget(String month, UpdateMonthlyBudgetRequest request, User user) {
        MonthlyBudget monthlyBudget = monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), month)
                .map(existingBudget -> {
                    existingBudget.setGeneralLimit(request.getGeneralLimit());
                    existingBudget.touch();
                    return existingBudget;
                })
                .orElseGet(() -> new MonthlyBudget(user, month, request.getGeneralLimit()));

        MonthlyBudget savedBudget = monthlyBudgetRepository.save(monthlyBudget);
        List<CategoryBudget> categoryBudgets = categoryBudgetRepository.findAllByMonthlyBudgetId(savedBudget.getId());

        return MonthlyBudgetResponse.fromEntity(savedBudget, categoryBudgets);
    }

    @Transactional
    public CategoryBudgetResponse upsertCategoryBudget(
            String month,
            UUID categoryId,
            UpdateCategoryBudgetRequest request,
            User user
    ) {
        MonthlyBudget monthlyBudget = monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), month)
                .orElseGet(() -> monthlyBudgetRepository.save(new MonthlyBudget(user, month, null)));
        Category category = categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new NotFoundException("Categoria nao encontrada"));
        CategoryBudget categoryBudget = categoryBudgetRepository.findByMonthlyBudgetIdAndCategoryId(monthlyBudget.getId(), categoryId)
                .map(existingBudget -> {
                    existingBudget.setLimitAmount(request.getLimitAmount());
                    existingBudget.touch();
                    return existingBudget;
                })
                .orElseGet(() -> new CategoryBudget(monthlyBudget, category, request.getLimitAmount()));

        return CategoryBudgetResponse.fromEntity(categoryBudgetRepository.save(categoryBudget));
    }

    @Transactional
    public void deleteCategoryBudget(String month, UUID categoryId, User user) {
        MonthlyBudget monthlyBudget = findMonthlyBudget(month, user);
        CategoryBudget categoryBudget = categoryBudgetRepository.findByMonthlyBudgetIdAndCategoryId(monthlyBudget.getId(), categoryId)
                .orElseThrow(() -> new NotFoundException("Orcamento da categoria nao encontrado"));

        categoryBudgetRepository.delete(categoryBudget);
    }

    @Transactional(readOnly = true)
    public BudgetSummaryResponse getSummary(String month, User user) {
        MonthlyBudget monthlyBudget = findMonthlyBudget(month, user);
        List<CategoryBudget> categoryBudgets = categoryBudgetRepository.findAllByMonthlyBudgetId(monthlyBudget.getId());
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        BigDecimal totalSpent = transactionRepository.sumNonCardExpensesByPeriod(user.getId(), startDate, endDate)
                .add(transactionRepository.sumCardExpensesForBudget(user.getId(), month));
        List<CategoryBudgetSummaryResponse> categories = categoryBudgets.stream()
                .map(categoryBudget -> toCategorySummary(categoryBudget, user, month, startDate, endDate))
                .toList();

        return new BudgetSummaryResponse(
                month,
                monthlyBudget.getGeneralLimit(),
                totalSpent,
                percent(totalSpent, monthlyBudget.getGeneralLimit()),
                categories
        );
    }

    private MonthlyBudget findMonthlyBudget(String month, User user) {
        return monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), month)
                .orElseThrow(() -> new NotFoundException("Orcamento mensal nao encontrado"));
    }

    private CategoryBudgetSummaryResponse toCategorySummary(
            CategoryBudget categoryBudget,
            User user,
            String month,
            LocalDate startDate,
            LocalDate endDate
    ) {
        UUID categoryId = categoryBudget.getCategory().getId();
        BigDecimal spentAmount = transactionRepository.sumNonCardExpensesByCategoryAndPeriod(
                user.getId(),
                categoryId,
                startDate,
                endDate
        ).add(transactionRepository.sumCardExpensesByCategoryForBudget(user.getId(), categoryId, month));

        return new CategoryBudgetSummaryResponse(
                categoryId,
                categoryBudget.getCategory().getName(),
                categoryBudget.getLimitAmount(),
                spentAmount,
                percent(spentAmount, categoryBudget.getLimitAmount())
        );
    }

    private BigDecimal percent(BigDecimal amount, BigDecimal limit) {
        if (limit == null || BigDecimal.ZERO.compareTo(limit) == 0) {
            return BigDecimal.ZERO;
        }

        return amount.multiply(new BigDecimal("100")).divide(limit, 2, RoundingMode.HALF_UP);
    }
}
