package com.eco.dashboard.service;

import com.eco.budget.model.MonthlyBudget;
import com.eco.budget.repository.MonthlyBudgetRepository;
import com.eco.dashboard.dto.DashboardBudgetResponse;
import com.eco.dashboard.dto.DashboardCashFlowResponse;
import com.eco.dashboard.dto.DashboardCategoryResponse;
import com.eco.dashboard.dto.DashboardGoalResponse;
import com.eco.dashboard.dto.DashboardMonthlyResponse;
import com.eco.goal.model.Goal;
import com.eco.goal.model.GoalStatus;
import com.eco.goal.repository.GoalRepository;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final GoalRepository goalRepository;

    public DashboardService(TransactionRepository transactionRepository,
                            MonthlyBudgetRepository monthlyBudgetRepository,
                            GoalRepository goalRepository) {
        this.transactionRepository = transactionRepository;
        this.monthlyBudgetRepository = monthlyBudgetRepository;
        this.goalRepository = goalRepository;
    }

    @Transactional(readOnly = true)
    public DashboardMonthlyResponse getMonthly(String month, User user) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        BigDecimal income = transactionRepository.sumAmountByTypeAndPeriod(
                user.getId(),
                TransactionType.INCOME,
                startDate,
                endDate
        );
        BigDecimal nonCardExpense = transactionRepository.sumNonCardExpensesByPeriod(user.getId(), startDate, endDate);
        BigDecimal creditCardTotal = transactionRepository.sumCardExpensesForBudget(user.getId(), month);
        BigDecimal expense = nonCardExpense.add(creditCardTotal);
        BigDecimal result = income.subtract(expense);
        DashboardBudgetResponse budget = buildBudget(month, user, expense);
        List<DashboardGoalResponse> goals = goalRepository
                .findAllByUserIdAndStatusNotOrderByCreatedAtDesc(user.getId(), GoalStatus.ARCHIVED)
                .stream()
                .map(this::toDashboardGoal)
                .toList();

        return new DashboardMonthlyResponse(month, income, expense, result, creditCardTotal, budget, goals);
    }

    @Transactional(readOnly = true)
    public List<DashboardCategoryResponse> getCategories(String month, User user) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        Map<UUID, CategoryAccumulator> totals = new LinkedHashMap<>();

        mergeCategoryRows(totals, transactionRepository.sumNonCardExpensesGroupedByCategory(user.getId(), startDate, endDate));
        mergeCategoryRows(totals, transactionRepository.sumCardExpensesGroupedByCategory(user.getId(), month));

        BigDecimal totalExpense = totals.values()
                .stream()
                .map(CategoryAccumulator::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totals.values()
                .stream()
                .map(category -> new DashboardCategoryResponse(
                        category.categoryId(),
                        category.categoryName(),
                        category.total(),
                        percent(category.total(), totalExpense)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DashboardCashFlowResponse> getCashFlow(String from, String to, User user) {
        YearMonth start = YearMonth.parse(from);
        YearMonth end = YearMonth.parse(to);
        List<DashboardCashFlowResponse> response = new ArrayList<>();

        for (YearMonth current = start; !current.isAfter(end); current = current.plusMonths(1)) {
            LocalDate startDate = current.atDay(1);
            LocalDate endDate = current.atEndOfMonth();
            String month = current.toString();
            BigDecimal income = transactionRepository.sumAmountByTypeAndPeriod(
                    user.getId(),
                    TransactionType.INCOME,
                    startDate,
                    endDate
            );
            BigDecimal expense = transactionRepository.sumNonCardExpensesByPeriod(user.getId(), startDate, endDate)
                    .add(transactionRepository.sumCardExpensesForBudget(user.getId(), month));

            response.add(new DashboardCashFlowResponse(month, income, expense, income.subtract(expense)));
        }

        return response;
    }

    private DashboardBudgetResponse buildBudget(String month, User user, BigDecimal spentAmount) {
        BigDecimal generalLimit = monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), month)
                .map(MonthlyBudget::getGeneralLimit)
                .orElse(null);

        return new DashboardBudgetResponse(generalLimit, spentAmount, percent(spentAmount, generalLimit));
    }

    private DashboardGoalResponse toDashboardGoal(Goal goal) {
        return new DashboardGoalResponse(goal.getId(), goal.getName(), percent(goal.getCurrentAmount(), goal.getTargetAmount()));
    }

    private void mergeCategoryRows(Map<UUID, CategoryAccumulator> totals, List<Object[]> rows) {
        for (Object[] row : rows) {
            UUID categoryId = (UUID) row[0];
            String categoryName = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            totals.merge(
                    categoryId,
                    new CategoryAccumulator(categoryId, categoryName, amount),
                    (existing, incoming) -> new CategoryAccumulator(
                            existing.categoryId(),
                            existing.categoryName(),
                            existing.total().add(incoming.total())
                    )
            );
        }
    }

    private BigDecimal percent(BigDecimal amount, BigDecimal limit) {
        if (limit == null || BigDecimal.ZERO.compareTo(limit) == 0) {
            return BigDecimal.ZERO;
        }

        return amount.multiply(new BigDecimal("100")).divide(limit, 2, RoundingMode.HALF_UP);
    }

    private record CategoryAccumulator(UUID categoryId, String categoryName, BigDecimal total) {
    }
}

