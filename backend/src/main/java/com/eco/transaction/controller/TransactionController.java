package com.eco.transaction.controller;

import com.eco.transaction.dto.CardSummaryResponse;
import com.eco.transaction.dto.CreateInstallmentTransactionRequest;
import com.eco.transaction.dto.CreateTransactionRequest;
import com.eco.transaction.dto.CreateTransferTransactionRequest;
import com.eco.transaction.dto.InstallmentTransactionResponse;
import com.eco.transaction.dto.TransactionPageResponse;
import com.eco.transaction.dto.TransactionResponse;
import com.eco.transaction.dto.UpdateTransactionRequest;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.service.TransactionService;
import com.eco.user.model.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public TransactionPageResponse findAll(
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return transactionService.findAll(accountId, categoryId, type, startDate, endDate, active, pageable, user);
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return transactionService.findById(id, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@RequestBody @Valid CreateTransactionRequest request, @AuthenticationPrincipal User user) {
        return transactionService.create(request, user);
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransfer(
            @RequestBody @Valid CreateTransferTransactionRequest request,
            @AuthenticationPrincipal User user
    ) {
        return transactionService.createTransfer(request, user);
    }

    @PostMapping("/installments")
    @ResponseStatus(HttpStatus.CREATED)
    public InstallmentTransactionResponse createInstallments(
            @RequestBody @Valid CreateInstallmentTransactionRequest request,
            @AuthenticationPrincipal User user
    ) {
        return transactionService.createInstallments(request, user);
    }

    @GetMapping("/card-summary")
    public CardSummaryResponse getCardSummary(
            @RequestParam String billingMonth,
            @AuthenticationPrincipal User user
    ) {
        return transactionService.getCardSummary(billingMonth, user);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTransactionRequest request,
            @AuthenticationPrincipal User user
    ) {
        return transactionService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        transactionService.deactivate(id, user);
    }
}
