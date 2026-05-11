package com.eco.budget.service;

import com.eco.budget.dto.BudgetSummaryResponse;
import com.eco.budget.dto.CategoryBudgetResponse;
import com.eco.budget.dto.MonthlyBudgetResponse;
import com.eco.budget.dto.UpdateCategoryBudgetRequest;
import com.eco.budget.dto.UpdateMonthlyBudgetRequest;
import com.eco.budget.model.CategoryBudget;
import com.eco.budget.model.MonthlyBudget;
import com.eco.budget.repository.CategoryBudgetRepository;
import com.eco.budget.repository.MonthlyBudgetRepository;
import com.eco.category.model.Category;
import com.eco.category.model.CategoryKind;
import com.eco.category.repository.CategoryRepository;
import com.eco.common.exception.NotFoundException;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private MonthlyBudgetRepository monthlyBudgetRepository;

    @Mock
    private CategoryBudgetRepository categoryBudgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetService budgetService;

    private final User user = new User("Usuario Dev", "dev@eco.com", "hash");

    @Test
    void upsertMonthlyBudgetShouldCreateBudgetWhenItDoesNotExist() {
        UpdateMonthlyBudgetRequest request = updateMonthlyBudgetRequest("4000.00");

        when(monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), "2026-05")).thenReturn(Optional.empty());
        when(monthlyBudgetRepository.save(any(MonthlyBudget.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoryBudgetRepository.findAllByMonthlyBudgetId(any())).thenReturn(List.of());

        MonthlyBudgetResponse response = budgetService.upsertMonthlyBudget("2026-05", request, user);

        assertThat(response.getMonth()).isEqualTo("2026-05");
        assertThat(response.getGeneralLimit()).isEqualByComparingTo(new BigDecimal("4000.00"));
        assertThat(response.getCategories()).isEmpty();
    }

    @Test
    void upsertCategoryBudgetShouldCreateMonthlyBudgetWhenNeeded() {
        UUID categoryId = UUID.randomUUID();
        UpdateCategoryBudgetRequest request = updateCategoryBudgetRequest("1200.00");
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);
        category.setId(categoryId);

        when(monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), "2026-05")).thenReturn(Optional.empty());
        when(monthlyBudgetRepository.save(any(MonthlyBudget.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoryRepository.findByIdAndUserId(categoryId, user.getId())).thenReturn(Optional.of(category));
        when(categoryBudgetRepository.findByMonthlyBudgetIdAndCategoryId(any(), any())).thenReturn(Optional.empty());
        when(categoryBudgetRepository.save(any(CategoryBudget.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryBudgetResponse response = budgetService.upsertCategoryBudget("2026-05", categoryId, request, user);

        assertThat(response.getCategoryId()).isEqualTo(categoryId);
        assertThat(response.getCategoryName()).isEqualTo("Alimentacao");
        assertThat(response.getLimitAmount()).isEqualByComparingTo(new BigDecimal("1200.00"));
    }

    @Test
    void getSummaryShouldConsiderNonCardByOccurredAtAndCardByBillingMonth() {
        String month = "2026-05";
        UUID categoryId = UUID.randomUUID();
        MonthlyBudget monthlyBudget = new MonthlyBudget(user, month, new BigDecimal("4000.00"));
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);
        category.setId(categoryId);
        CategoryBudget categoryBudget = new CategoryBudget(monthlyBudget, category, new BigDecimal("1200.00"));
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 31);

        when(monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), month)).thenReturn(Optional.of(monthlyBudget));
        when(categoryBudgetRepository.findAllByMonthlyBudgetId(monthlyBudget.getId())).thenReturn(List.of(categoryBudget));
        when(transactionRepository.sumNonCardExpensesByPeriod(user.getId(), startDate, endDate))
                .thenReturn(new BigDecimal("700.00"));
        when(transactionRepository.sumCardExpensesForBudget(user.getId(), month))
                .thenReturn(new BigDecimal("250.00"));
        when(transactionRepository.sumNonCardExpensesByCategoryAndPeriod(user.getId(), categoryId, startDate, endDate))
                .thenReturn(new BigDecimal("600.00"));
        when(transactionRepository.sumCardExpensesByCategoryForBudget(user.getId(), categoryId, month))
                .thenReturn(new BigDecimal("350.00"));

        BudgetSummaryResponse response = budgetService.getSummary(month, user);

        assertThat(response.getTotalSpent()).isEqualByComparingTo(new BigDecimal("950.00"));
        assertThat(response.getGeneralUsagePercent()).isEqualByComparingTo(new BigDecimal("23.75"));
        assertThat(response.getCategories()).hasSize(1);
        assertThat(response.getCategories().getFirst().getSpentAmount()).isEqualByComparingTo(new BigDecimal("950.00"));
        assertThat(response.getCategories().getFirst().getUsagePercent()).isEqualByComparingTo(new BigDecimal("79.17"));
    }

    @Test
    void getSummaryShouldShowCategoryAboveLimit() {
        String month = "2026-05";
        UUID categoryId = UUID.randomUUID();
        MonthlyBudget monthlyBudget = new MonthlyBudget(user, month, new BigDecimal("1000.00"));
        Category category = new Category("Lazer", CategoryKind.EXPENSE, "#E86F51", "ticket", user);
        category.setId(categoryId);
        CategoryBudget categoryBudget = new CategoryBudget(monthlyBudget, category, new BigDecimal("200.00"));
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 31);

        when(monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), month)).thenReturn(Optional.of(monthlyBudget));
        when(categoryBudgetRepository.findAllByMonthlyBudgetId(monthlyBudget.getId())).thenReturn(List.of(categoryBudget));
        when(transactionRepository.sumNonCardExpensesByPeriod(user.getId(), startDate, endDate)).thenReturn(new BigDecimal("250.00"));
        when(transactionRepository.sumCardExpensesForBudget(user.getId(), month)).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumNonCardExpensesByCategoryAndPeriod(user.getId(), categoryId, startDate, endDate))
                .thenReturn(new BigDecimal("250.00"));
        when(transactionRepository.sumCardExpensesByCategoryForBudget(user.getId(), categoryId, month)).thenReturn(BigDecimal.ZERO);

        BudgetSummaryResponse response = budgetService.getSummary(month, user);

        assertThat(response.getCategories().getFirst().getSpentAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(response.getCategories().getFirst().getUsagePercent()).isEqualByComparingTo(new BigDecimal("125.00"));
    }

    @Test
    void findByMonthShouldThrowNotFoundWhenBudgetDoesNotExist() {
        when(monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), "2026-05")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> budgetService.findByMonth("2026-05", user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Orcamento mensal nao encontrado");
    }

    @Test
    void deleteCategoryBudgetShouldDeleteWhenExists() {
        UUID categoryId = UUID.randomUUID();
        MonthlyBudget monthlyBudget = new MonthlyBudget(user, "2026-05", new BigDecimal("4000.00"));
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);
        CategoryBudget categoryBudget = new CategoryBudget(monthlyBudget, category, new BigDecimal("1200.00"));

        when(monthlyBudgetRepository.findByUserIdAndMonth(user.getId(), "2026-05")).thenReturn(Optional.of(monthlyBudget));
        when(categoryBudgetRepository.findByMonthlyBudgetIdAndCategoryId(monthlyBudget.getId(), categoryId))
                .thenReturn(Optional.of(categoryBudget));

        budgetService.deleteCategoryBudget("2026-05", categoryId, user);

        verify(categoryBudgetRepository).delete(categoryBudget);
    }

    private UpdateMonthlyBudgetRequest updateMonthlyBudgetRequest(String generalLimit) {
        UpdateMonthlyBudgetRequest request = new UpdateMonthlyBudgetRequest();
        ReflectionTestUtils.setField(request, "generalLimit", new BigDecimal(generalLimit));
        return request;
    }

    private UpdateCategoryBudgetRequest updateCategoryBudgetRequest(String limitAmount) {
        UpdateCategoryBudgetRequest request = new UpdateCategoryBudgetRequest();
        ReflectionTestUtils.setField(request, "limitAmount", new BigDecimal(limitAmount));
        return request;
    }
}
