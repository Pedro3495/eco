package com.eco.transaction.dto;

import java.math.BigDecimal;

public class CardSummaryResponse {

    private String billingMonth;
    private BigDecimal total;
    private long transactionsCount;

    public CardSummaryResponse(String billingMonth, BigDecimal total, long transactionsCount) {
        this.billingMonth = billingMonth;
        this.total = total;
        this.transactionsCount = transactionsCount;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public long getTransactionsCount() {
        return transactionsCount;
    }
}
