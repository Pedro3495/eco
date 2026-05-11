package com.eco.security;

import com.eco.account.repository.AccountRepository;
import com.eco.account.service.AccountService;
import com.eco.category.repository.CategoryRepository;
import com.eco.category.service.CategoryService;
import com.eco.common.exception.NotFoundException;
import com.eco.report.service.ReportService;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.transaction.service.TransactionService;
import com.eco.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialDataUserScopeIsolationTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @InjectMocks
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    @InjectMocks
    private ReportService reportService;

    private final User userA = new User("Usuario A", "a@eco.com", "hash");

    @Test
    void accountFromAnotherUserShouldNotBeAccessible() {
        UUID accountIdFromUserB = UUID.randomUUID();

        when(accountRepository.findByIdAndUserId(accountIdFromUserB, userA.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(accountIdFromUserB, userA))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Conta nao encontrada");

        verify(accountRepository).findByIdAndUserId(accountIdFromUserB, userA.getId());
    }

    @Test
    void categoryFromAnotherUserShouldNotBeAccessible() {
        UUID categoryIdFromUserB = UUID.randomUUID();

        when(categoryRepository.findByIdAndUserId(categoryIdFromUserB, userA.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(categoryIdFromUserB, userA))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Categoria nao encontrada");

        verify(categoryRepository).findByIdAndUserId(categoryIdFromUserB, userA.getId());
    }

    @Test
    void transactionFromAnotherUserShouldNotBeAccessible() {
        UUID transactionIdFromUserB = UUID.randomUUID();

        when(transactionRepository.findOne(anyTransactionSpecification())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(transactionIdFromUserB, userA))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Transacao nao encontrada");

        verify(transactionRepository).findOne(anyTransactionSpecification());
    }

    @Test
    void monthlySummaryShouldUseAuthenticatedUserOnly() {
        int year = 2026;
        int month = 5;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 31);

        when(transactionRepository.sumAmountByTypeAndPeriod(userA.getId(), TransactionType.INCOME, startDate, endDate))
                .thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.sumAmountByTypeAndPeriod(userA.getId(), TransactionType.EXPENSE, startDate, endDate))
                .thenReturn(new BigDecimal("250.00"));

        var response = reportService.getMonthlySummary(year, month, userA);

        assertThat(response.getIncome()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(response.getExpense()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("750.00"));

        verify(transactionRepository).sumAmountByTypeAndPeriod(userA.getId(), TransactionType.INCOME, startDate, endDate);
        verify(transactionRepository).sumAmountByTypeAndPeriod(userA.getId(), TransactionType.EXPENSE, startDate, endDate);
    }

    private Specification<com.eco.transaction.model.Transaction> anyTransactionSpecification() {
        return any();
    }
}
