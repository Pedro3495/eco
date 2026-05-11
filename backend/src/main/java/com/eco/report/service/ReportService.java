package com.eco.report.service;

import com.eco.report.dto.MonthlySummaryResponse;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(int year, int month, User user) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal income = transactionRepository.sumAmountByTypeAndPeriod(
                user.getId(),
                TransactionType.INCOME,
                startDate,
                endDate
        );
        BigDecimal expense = transactionRepository.sumAmountByTypeAndPeriod(
                user.getId(),
                TransactionType.EXPENSE,
                startDate,
                endDate
        );
        BigDecimal balance = income.subtract(expense);

        return new MonthlySummaryResponse(income, expense, balance);
    }
}
