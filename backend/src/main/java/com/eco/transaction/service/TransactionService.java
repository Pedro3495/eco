package com.eco.transaction.service;

import com.eco.account.model.AccountType;
import com.eco.account.model.Account;
import com.eco.account.repository.AccountRepository;
import com.eco.category.model.Category;
import com.eco.category.model.CategoryKind;
import com.eco.category.repository.CategoryRepository;
import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import com.eco.transaction.dto.CardSummaryResponse;
import com.eco.transaction.dto.CreateInstallmentTransactionRequest;
import com.eco.transaction.dto.CreateTransactionRequest;
import com.eco.transaction.dto.CreateTransferTransactionRequest;
import com.eco.transaction.dto.InstallmentTransactionResponse;
import com.eco.transaction.dto.TransactionPageResponse;
import com.eco.transaction.dto.TransactionResponse;
import com.eco.transaction.dto.UpdateTransactionRequest;
import com.eco.transaction.model.Transaction;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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
            Pageable pageable,
            User user
    ) {
        Page<Transaction> transactions = transactionRepository.findAll(
                buildFilter(accountId, categoryId, type, startDate, endDate, active, user),
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
    public TransactionResponse findById(UUID id, User user) {
        Transaction transaction = transactionRepository.findOne(idEquals(id).and(userEquals(user)))
                .orElseThrow(() -> new NotFoundException("Transacao nao encontrada"));

        return TransactionResponse.fromEntity(transaction);
    }

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request, User user) {
        if (request.getType() == TransactionType.TRANSFER) {
            throw new BusinessException("Use o endpoint de transferencias");
        }

        Account account = findAccount(request.getAccountId(), user);
        Category category = findCategory(request.getCategoryId(), user);
        validateCategoryCompatibility(category, request.getType());
        validateBillingMonth(account, request.getType(), request.getBillingMonth());

        Transaction transaction = new Transaction(
                request.getDescription(),
                request.getAmount(),
                request.getType(),
                request.getOccurredAt(),
                account,
                category,
                user,
                request.getNote()
        );
        transaction.setBillingMonth(request.getBillingMonth());

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.fromEntity(savedTransaction);
    }

    @Transactional
    public TransactionResponse createTransfer(CreateTransferTransactionRequest request, User user) {
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BusinessException("Conta de origem e destino devem ser diferentes");
        }

        Account fromAccount = findAccount(request.getFromAccountId(), user);
        Account toAccount = findAccount(request.getToAccountId(), user);

        Transaction transaction = Transaction.transfer(
                request.getDescription(),
                request.getAmount(),
                request.getOccurredAt(),
                fromAccount,
                toAccount,
                user,
                request.getNote()
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.fromEntity(savedTransaction);
    }

    @Transactional
    public InstallmentTransactionResponse createInstallments(CreateInstallmentTransactionRequest request, User user) {
        Account account = findAccount(request.getAccountId(), user);
        Category category = findCategory(request.getCategoryId(), user);
        validateCategoryCompatibility(category, TransactionType.EXPENSE);
        validateBillingMonth(account, TransactionType.EXPENSE, request.getFirstBillingMonth());

        UUID installmentGroupId = UUID.randomUUID();
        List<BigDecimal> amounts = splitAmount(request.getTotalAmount(), request.getInstallmentTotal());
        YearMonth firstBillingMonth = YearMonth.parse(request.getFirstBillingMonth());
        List<Transaction> transactions = new ArrayList<>();

        for (int index = 0; index < request.getInstallmentTotal(); index++) {
            int installmentNumber = index + 1;
            Transaction transaction = new Transaction(
                    request.getDescription() + " " + installmentNumber + "/" + request.getInstallmentTotal(),
                    amounts.get(index),
                    TransactionType.EXPENSE,
                    request.getFirstOccurredAt().plusMonths(index),
                    account,
                    category,
                    user,
                    request.getNote()
            );
            transaction.setBillingMonth(firstBillingMonth.plusMonths(index).toString());
            transaction.setInstallmentGroupId(installmentGroupId);
            transaction.setInstallmentNumber(installmentNumber);
            transaction.setInstallmentTotal(request.getInstallmentTotal());
            transactions.add(transaction);
        }

        List<TransactionResponse> items = transactionRepository.saveAll(transactions)
                .stream()
                .map(TransactionResponse::fromEntity)
                .toList();

        return new InstallmentTransactionResponse(installmentGroupId, items);
    }

    @Transactional(readOnly = true)
    public CardSummaryResponse getCardSummary(String billingMonth, User user) {
        BigDecimal total = transactionRepository.sumCardExpensesByBillingMonth(user.getId(), billingMonth);
        long transactionsCount = transactionRepository.countByUserIdAndTypeAndActiveTrueAndBillingMonth(
                user.getId(),
                TransactionType.EXPENSE,
                billingMonth
        );

        return new CardSummaryResponse(billingMonth, total, transactionsCount);
    }

    @Transactional
    public TransactionResponse update(UUID id, UpdateTransactionRequest request, User user) {
        Transaction transaction = transactionRepository.findOne(idEquals(id).and(userEquals(user)))
                .orElseThrow(() -> new NotFoundException("Transacao nao encontrada"));

        Account account = findAccount(request.getAccountId(), user);
        Category category = findCategory(request.getCategoryId(), user);
        validateCategoryCompatibility(category, request.getType());
        validateBillingMonth(account, request.getType(), request.getBillingMonth());

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setOccurredAt(request.getOccurredAt());
        transaction.setBillingMonth(request.getBillingMonth());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setNote(request.getNote());
        transaction.setActive(request.getActive());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.fromEntity(updatedTransaction);
    }

    @Transactional
    public void deactivate(UUID id, User user) {
        Transaction transaction = transactionRepository.findOne(idEquals(id).and(userEquals(user)))
                .orElseThrow(() -> new NotFoundException("Transacao nao encontrada"));

        transaction.setActive(false);

        transactionRepository.save(transaction);
    }

    private Account findAccount(UUID id, User user) {
        return accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));
    }

    private Category findCategory(UUID id, User user) {
        return categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Categoria nao encontrada"));
    }

    private void validateCategoryCompatibility(Category category, TransactionType transactionType) {
        if (transactionType == TransactionType.TRANSFER) {
            throw new BusinessException("Use o endpoint de transferencias");
        }

        if (category.getKind() == CategoryKind.BOTH) {
            return;
        }

        if (category.getKind().name().equals(transactionType.name())) {
            return;
        }

        throw new BusinessException("Categoria incompativel com o tipo da transacao");
    }

    private void validateBillingMonth(Account account, TransactionType type, String billingMonth) {
        if (account.getType() == AccountType.CREDIT_CARD && type == TransactionType.EXPENSE && billingMonth == null) {
            throw new BusinessException("Mes da fatura e obrigatorio para despesa de cartao");
        }

        if (account.getType() != AccountType.CREDIT_CARD && billingMonth != null) {
            throw new BusinessException("Mes da fatura deve ser usado apenas em cartao");
        }
    }

    private List<BigDecimal> splitAmount(BigDecimal totalAmount, int installmentTotal) {
        BigDecimal baseAmount = totalAmount.divide(BigDecimal.valueOf(installmentTotal), 2, RoundingMode.DOWN);
        BigDecimal assignedAmount = baseAmount.multiply(BigDecimal.valueOf(installmentTotal - 1));
        BigDecimal lastAmount = totalAmount.subtract(assignedAmount);
        List<BigDecimal> amounts = new ArrayList<>();

        for (int index = 1; index < installmentTotal; index++) {
            amounts.add(baseAmount);
        }

        amounts.add(lastAmount);
        return amounts;
    }

    private Specification<Transaction> buildFilter(
            UUID accountId,
            UUID categoryId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Boolean active,
            User user
    ) {
        return Specification.allOf(
                userEquals(user),
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

    private Specification<Transaction> idEquals(UUID id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }

    private Specification<Transaction> userEquals(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), user.getId());
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
