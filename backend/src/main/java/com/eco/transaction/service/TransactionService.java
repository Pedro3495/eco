package com.eco.transaction.service;

import com.eco.account.model.Account;
import com.eco.account.repository.AccountRepository;
import com.eco.category.model.Category;
import com.eco.category.model.CategoryKind;
import com.eco.category.repository.CategoryRepository;
import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import com.eco.transaction.dto.CreateTransactionRequest;
import com.eco.transaction.dto.TransactionPageResponse;
import com.eco.transaction.dto.TransactionResponse;
import com.eco.transaction.dto.UpdateTransactionRequest;
import com.eco.transaction.model.Transaction;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public TransactionPageResponse findAll(
            UUID accountId,
            UUID categoryId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Boolean active,
            Pageable pageable
    ) {
        Page<Transaction> transactions = transactionRepository.findAll(
                buildFilter(accountId, categoryId, type, startDate, endDate, active),
                pageable
        );

        List<TransactionResponse> items = transactions.getContent()
                .stream()
                .map(TransactionResponse::fromEntity)
                .toList();

        return new TransactionPageResponse(
                items,
                transactions.getNumber(),
                transactions.getSize(),
                transactions.getTotalElements(),
                transactions.getTotalPages()
        );
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
        validateCategoryCompatibility(category, request.getType());

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
        validateCategoryCompatibility(category, request.getType());

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

    private void validateCategoryCompatibility(Category category, TransactionType transactionType) {
        if (category.getKind() == CategoryKind.BOTH) {
            return;
        }

        if (category.getKind().name().equals(transactionType.name())) {
            return;
        }

        throw new BusinessException("Categoria incompativel com o tipo da transacao");
    }

    private Specification<Transaction> buildFilter(
            UUID accountId,
            UUID categoryId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Boolean active
    ) {
        return Specification.allOf(
                accountIdEquals(accountId),
                categoryIdEquals(categoryId),
                typeEquals(type),
                occurredAtGreaterThanOrEqualTo(startDate),
                occurredAtLessThanOrEqualTo(endDate),
                activeEquals(active)
        );
    }

    private Specification<Transaction> accountIdEquals(UUID accountId) {
        return (root, query, criteriaBuilder) -> accountId == null
                ? null
                : criteriaBuilder.equal(root.get("account").get("id"), accountId);
    }

    private Specification<Transaction> categoryIdEquals(UUID categoryId) {
        return (root, query, criteriaBuilder) -> categoryId == null
                ? null
                : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Transaction> typeEquals(TransactionType type) {
        return (root, query, criteriaBuilder) -> type == null
                ? null
                : criteriaBuilder.equal(root.get("type"), type);
    }

    private Specification<Transaction> occurredAtGreaterThanOrEqualTo(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> startDate == null
                ? null
                : criteriaBuilder.greaterThanOrEqualTo(root.get("occurredAt"), startDate);
    }

    private Specification<Transaction> occurredAtLessThanOrEqualTo(LocalDate endDate) {
        return (root, query, criteriaBuilder) -> endDate == null
                ? null
                : criteriaBuilder.lessThanOrEqualTo(root.get("occurredAt"), endDate);
    }

    private Specification<Transaction> activeEquals(Boolean active) {
        return (root, query, criteriaBuilder) -> active == null
                ? null
                : criteriaBuilder.equal(root.get("active"), active);
    }
}
