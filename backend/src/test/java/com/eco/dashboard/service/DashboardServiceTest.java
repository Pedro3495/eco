package com.eco.dashboard.service;

import com.eco.budget.model.MonthlyBudget;
import com.eco.budget.repository.MonthlyBudgetRepository;
import com.eco.dashboard.dto.DashboardCashFlowResponse;
import com.eco.dashboard.dto.DashboardCategoryResponse;
import com.eco.dashboard.dto.DashboardMonthlyResponse;
import com.eco.goal.model.Goal;
import com.eco.goal.model.GoalStatus;
import com.eco.goal.repository.GoalRepository;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MonthlyBudgetRepository monthlyBudgetRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private final User user = new User("Usuario Dev", "dev@eco.com", "hash");

    @Test
    void getMonthlyShouldUseCardExpensesByBillingMonthAndIncludeBudgetAndGoals() {
        String month = "2026-05";
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 31);
        MonthlyBudget budget = new MonthlyBudget(user, month, new BigDecimal("4000.00"));
        Goal goal = new Goal("Reserva", new BigDecimal("10000.00"), new BigDecimal("2500.00"), null, user);

        when(transactionRepository.sumAmountByTypeAndPeriod(user.getId(), TransactionType.INCOME, startDate, endDate))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumNonCardExpensesByPeriod(user.getId(), startDate, endDate))
                .thenReturn(new BigDecimal("1200.00"));
        when(transactionRepository.sumCardExpensesForBudget(user.getId(), month))
                .thenReturn(new BigDecimal("800.00"));
        when(monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), month)).thenReturn(Optional.of(budget));
        when(goalRepository.findAllByUserIdAndStatusNotOrderByCreatedAtDesc(user.getId(), GoalStatus.ARCHIVED))
                .thenReturn(List.of(goal));

        DashboardMonthlyResponse response = dashboardService.getMonthly(month, user);

        assertThat(response.getIncome()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(response.getExpense()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(response.getResult()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(response.getCreditCardTotal()).isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(response.getBudget().getGeneralLimit()).isEqualByComparingTo(new BigDecimal("4000.00"));
        assertThat(response.getBudget().getSpentAmount()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(response.getBudget().getUsagePercent()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(response.getGoals()).hasSize(1);
        assertThat(response.getGoals().getFirst().getProgressPercent()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void getCategoriesShouldMergeNonCardAndCardExpensesByCategory() {
        UUID foodId = UUID.randomUUID();
        UUID leisureId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 31);

        when(transactionRepository.sumNonCardExpensesGroupedByCategory(user.getId(), startDate, endDate))
                .thenReturn(List.<Object[]>of(new Object[]{foodId, "Alimentacao", new BigDecimal("600.00")}));
        when(transactionRepository.sumCardExpensesGroupedByCategory(user.getId(), "2026-05"))
                .thenReturn(List.<Object[]>of(
                        new Object[]{foodId, "Alimentacao", new BigDecimal("400.00")},
                        new Object[]{leisureId, "Lazer", new BigDecimal("500.00")}
                ));

        List<DashboardCategoryResponse> response = dashboardService.getCategories("2026-05", user);

        assertThat(response).hasSize(2);
        assertThat(response.getFirst().getCategoryId()).isEqualTo(foodId);
        assertThat(response.getFirst().getTotal()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(response.getFirst().getPercentage()).isEqualByComparingTo(new BigDecimal("66.67"));
        assertThat(response.get(1).getCategoryId()).isEqualTo(leisureId);
        assertThat(response.get(1).getTotal()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(response.get(1).getPercentage()).isEqualByComparingTo(new BigDecimal("33.33"));
    }

    @Test
    void getCashFlowShouldReturnOneItemPerMonth() {
        LocalDate mayStart = LocalDate.of(2026, 5, 1);
        LocalDate mayEnd = LocalDate.of(2026, 5, 31);
        LocalDate juneStart = LocalDate.of(2026, 6, 1);
        LocalDate juneEnd = LocalDate.of(2026, 6, 30);

        when(transactionRepository.sumAmountByTypeAndPeriod(user.getId(), TransactionType.INCOME, mayStart, mayEnd))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumNonCardExpensesByPeriod(user.getId(), mayStart, mayEnd))
                .thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.sumCardExpensesForBudget(user.getId(), "2026-05"))
                .thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.sumAmountByTypeAndPeriod(user.getId(), TransactionType.INCOME, juneStart, juneEnd))
                .thenReturn(new BigDecimal("5200.00"));
        when(transactionRepository.sumNonCardExpensesByPeriod(user.getId(), juneStart, juneEnd))
                .thenReturn(new BigDecimal("1200.00"));
        when(transactionRepository.sumCardExpensesForBudget(user.getId(), "2026-06"))
                .thenReturn(new BigDecimal("700.00"));

        List<DashboardCashFlowResponse> response = dashboardService.getCashFlow("2026-05", "2026-06", user);

        assertThat(response).hasSize(2);
        assertThat(response.getFirst().getMonth()).isEqualTo("2026-05");
        assertThat(response.getFirst().getExpense()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(response.getFirst().getResult()).isEqualByComparingTo(new BigDecimal("3500.00"));
        assertThat(response.get(1).getMonth()).isEqualTo("2026-06");
        assertThat(response.get(1).getExpense()).isEqualByComparingTo(new BigDecimal("1900.00"));
        assertThat(response.get(1).getResult()).isEqualByComparingTo(new BigDecimal("3300.00"));
    }
}
