package com.eco.transaction.service;

import com.eco.account.model.Account;
import com.eco.account.repository.AccountRepository;
import com.eco.category.model.Category;
import com.eco.category.repository.CategoryRepository;
import com.eco.common.exception.NotFoundException;
import com.eco.transaction.dto.CreateTransactionRequest;
import com.eco.transaction.dto.TransactionResponse;
import com.eco.transaction.dto.UpdateTransactionRequest;
import com.eco.transaction.model.Transaction;
import com.eco.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll() {
        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transacao nao encontrada"));

        return TransactionResponse.fromEntity(transaction);
    }

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request) {
        Account account = findAccount(request.getAccountId());
        Category category = findCategory(request.getCategoryId());

        Transaction transaction = new Transaction(
                request.getDescription(),
                request.getAmount(),
                request.getType(),
                request.getOccurredAt(),
                account,
                category,
                request.getNote()
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.fromEntity(savedTransaction);
    }

    @Transactional
    public TransactionResponse update(UUID id, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transacao nao encontrada"));

        Account account = findAccount(request.getAccountId());
        Category category = findCategory(request.getCategoryId());

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setOccurredAt(request.getOccurredAt());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setNote(request.getNote());
        transaction.setActive(request.getActive());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.fromEntity(updatedTransaction);
    }

    @Transactional
    public void deactivate(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transacao nao encontrada"));

        transaction.setActive(false);

        transactionRepository.save(transaction);
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));
    }

    private Category findCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria nao encontrada"));
    }
}
