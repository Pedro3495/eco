package com.eco.transaction.dto;

import java.util.List;
import java.util.UUID;

public class InstallmentTransactionResponse {

    private UUID installmentGroupId;
    private List<TransactionResponse> items;

    public InstallmentTransactionResponse(UUID installmentGroupId, List<TransactionResponse> items) {
        this.installmentGroupId = installmentGroupId;
        this.items = items;
    }

    public UUID getInstallmentGroupId() {
        return installmentGroupId;
    }

    public List<TransactionResponse> getItems() {
        return items;
    }
}
