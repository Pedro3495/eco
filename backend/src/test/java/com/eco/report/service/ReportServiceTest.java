package com.eco.report.service;

import com.eco.report.dto.MonthlySummaryResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportService reportService;

    private final User user = new User("Usuario Dev", "dev@eco.com", "hash");

    @Test
    void getMonthlySummaryShouldReturnIncomeExpenseAndBalance() {
        int year = 2026;
        int month = 5;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        when(transactionRepository.sumAmountByTypeAndPeriod(user.getId(), TransactionType.INCOME, startDate, endDate))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumAmountByTypeAndPeriod(user.getId(), TransactionType.EXPENSE, startDate, endDate))
                .thenReturn(new BigDecimal("2300.00"));

        MonthlySummaryResponse response = reportService.getMonthlySummary(year, month, user);

        assertThat(response.getIncome()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(response.getExpense()).isEqualByComparingTo(new BigDecimal("2300.00"));
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("2700.00"));

        verify(transactionRepository).sumAmountByTypeAndPeriod(user.getId(), TransactionType.INCOME, startDate, endDate);
        verify(transactionRepository).sumAmountByTypeAndPeriod(user.getId(), TransactionType.EXPENSE, startDate, endDate);
    }

    @Test
    void getMonthlySummaryShouldUseLastDayOfFebruaryInLeapYear() {
        int year = 2024;
        int month = 2;

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 29);

        when(transactionRepository.sumAmountByTypeAndPeriod(user.getId(), TransactionType.INCOME, startDate, endDate))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumAmountByTypeAndPeriod(user.getId(), TransactionType.EXPENSE, startDate, endDate))
                .thenReturn(BigDecimal.ZERO);

        MonthlySummaryResponse response = reportService.getMonthlySummary(year, month, user);

        assertThat(response.getIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(transactionRepository).sumAmountByTypeAndPeriod(user.getId(), TransactionType.INCOME, startDate, endDate);
        verify(transactionRepository).sumAmountByTypeAndPeriod(user.getId(), TransactionType.EXPENSE, startDate, endDate);
    }

}
