package com.eco.transaction.service;

import com.eco.account.model.Account;
import com.eco.account.model.AccountType;
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
import com.eco.transaction.model.Transaction;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private final User user = new User("Usuario Dev", "dev@eco.com", "hash");

    @Test
    void findAllShouldReturnTransactions() {
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);
        Transaction transaction = new Transaction(
                "Mercado",
                new BigDecimal("50.00"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 5, 9),
                account,
                category,
                user,
                "Compra semanal"
        );

        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findAll(anyTransactionSpecification(), anyPageable()))
                .thenReturn(new PageImpl<>(List.of(transaction), pageable, 1));

        TransactionPageResponse response = transactionService.findAll(null, null, null, null, null, null, pageable, user);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().getFirst().getDescription()).isEqualTo("Mercado");
        assertThat(response.getItems().getFirst().getAccountName()).isEqualTo("Conta Principal");
        assertThat(response.getItems().getFirst().getCategoryName()).isEqualTo("Alimentacao");
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalItems()).isEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(1);
    }

    @Test
    void findAllShouldAcceptFilters() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2026, 5, 1);
        LocalDate endDate = LocalDate.of(2026, 5, 31);
        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findAll(anyTransactionSpecification(), anyPageable()))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        TransactionPageResponse response = transactionService.findAll(
                accountId,
                categoryId,
                TransactionType.EXPENSE,
                startDate,
                endDate,
                true,
                pageable,
                user
        );

        assertThat(response.getItems()).isEmpty();
        verify(transactionRepository).findAll(anyTransactionSpecification(), anyPageable());
    }

    @Test
    void findByIdShouldThrowNotFoundWhenTransactionDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(transactionRepository.findOne(anyTransactionSpecification())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(id, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Transacao nao encontrada");
    }

    @Test
    void createShouldSaveTransactionWhenRelationsExist() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);
        account.setId(accountId);
        category.setId(categoryId);

        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(categoryId, user.getId())).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.create(request, user);

        assertThat(response.getDescription()).isEqualTo("Mercado");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(response.getAccountId()).isEqualTo(accountId);
        assertThat(response.getCategoryId()).isEqualTo(categoryId);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createShouldThrowBusinessExceptionWhenCategoryIsIncompatibleWithTransactionType() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        Category category = new Category("Salario", CategoryKind.INCOME, "#2E8B57", "wallet", user);

        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(categoryId, user.getId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> transactionService.create(request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Categoria incompativel com o tipo da transacao");
    }

    @Test
    void createShouldAcceptBothCategoryForAnyTransactionType() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        Category category = new Category("Ajustes", CategoryKind.BOTH, "#64748B", "repeat", user);
        account.setId(accountId);
        category.setId(categoryId);

        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(categoryId, user.getId())).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.create(request, user);

        assertThat(response.getCategoryId()).isEqualTo(categoryId);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createShouldThrowNotFoundWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);

        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.create(request, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Conta nao encontrada");
    }

    @Test
    void createShouldRejectTransferTypeInRegularEndpoint() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);
        ReflectionTestUtils.setField(request, "type", TransactionType.TRANSFER);

        assertThatThrownBy(() -> transactionService.create(request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Use o endpoint de transferencias");
    }

    @Test
    void createTransferShouldSaveTransferWhenAccountsExist() {
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        CreateTransferTransactionRequest request = createTransferTransactionRequest(fromAccountId, toAccountId);
        Account fromAccount = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        Account toAccount = new Account("Reserva", AccountType.SAVINGS, BigDecimal.ZERO, user);
        fromAccount.setId(fromAccountId);
        toAccount.setId(toAccountId);

        when(accountRepository.findByIdAndUserId(fromAccountId, user.getId())).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdAndUserId(toAccountId, user.getId())).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.createTransfer(request, user);

        assertThat(response.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
        assertThat(response.getAccountId()).isEqualTo(fromAccountId);
        assertThat(response.getTransferAccountId()).isEqualTo(toAccountId);
        assertThat(response.getCategoryId()).isNull();
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransferShouldThrowBusinessExceptionWhenAccountsAreSame() {
        UUID accountId = UUID.randomUUID();
        CreateTransferTransactionRequest request = createTransferTransactionRequest(accountId, accountId);

        assertThatThrownBy(() -> transactionService.createTransfer(request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Conta de origem e destino devem ser diferentes");
    }

    @Test
    void createTransferShouldThrowNotFoundWhenDestinationAccountDoesNotExistForUser() {
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        CreateTransferTransactionRequest request = createTransferTransactionRequest(fromAccountId, toAccountId);
        Account fromAccount = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        fromAccount.setId(fromAccountId);

        when(accountRepository.findByIdAndUserId(fromAccountId, user.getId())).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdAndUserId(toAccountId, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransfer(request, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Conta nao encontrada");
    }

    @Test
    void createShouldRequireBillingMonthForCreditCardExpense() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);
        Account account = new Account("Cartao", AccountType.CREDIT_CARD, BigDecimal.ZERO, user);
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);

        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(categoryId, user.getId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> transactionService.create(request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Mes da fatura e obrigatorio para despesa de cartao");
    }

    @Test
    void createShouldRejectBillingMonthForNonCreditCardAccount() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);
        ReflectionTestUtils.setField(request, "billingMonth", "2026-06");
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils", user);

        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(categoryId, user.getId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> transactionService.create(request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Mes da fatura deve ser usado apenas em cartao");
    }

    @Test
    void createInstallmentsShouldCreateTwoFutureCardExpenses() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateInstallmentTransactionRequest request = createInstallmentRequest(accountId, categoryId, "100.00", 2);
        Account account = new Account("Cartao", AccountType.CREDIT_CARD, BigDecimal.ZERO, user);
        Category category = new Category("Eletronicos", CategoryKind.EXPENSE, "#E86F51", "shopping-bag", user);
        account.setId(accountId);
        category.setId(categoryId);

        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(categoryId, user.getId())).thenReturn(Optional.of(category));
        when(transactionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        InstallmentTransactionResponse response = transactionService.createInstallments(request, user);

        assertThat(response.getInstallmentGroupId()).isNotNull();
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems().get(0).getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(response.getItems().get(0).getBillingMonth()).isEqualTo("2026-06");
        assertThat(response.getItems().get(0).getInstallmentNumber()).isEqualTo(1);
        assertThat(response.getItems().get(1).getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(response.getItems().get(1).getBillingMonth()).isEqualTo("2026-07");
        assertThat(response.getItems().get(1).getInstallmentNumber()).isEqualTo(2);
    }

    @Test
    void createInstallmentsShouldPutRoundingDifferenceInLastInstallment() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateInstallmentTransactionRequest request = createInstallmentRequest(accountId, categoryId, "100.00", 3);
        Account account = new Account("Cartao", AccountType.CREDIT_CARD, BigDecimal.ZERO, user);
        Category category = new Category("Eletronicos", CategoryKind.EXPENSE, "#E86F51", "shopping-bag", user);
        account.setId(accountId);
        category.setId(categoryId);

        when(accountRepository.findByIdAndUserId(accountId, user.getId())).thenReturn(Optional.of(account));
        when(categoryRepository.findByIdAndUserId(categoryId, user.getId())).thenReturn(Optional.of(category));
        when(transactionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        InstallmentTransactionResponse response = transactionService.createInstallments(request, user);

        assertThat(response.getItems()).hasSize(3);
        assertThat(response.getItems().get(0).getAmount()).isEqualByComparingTo(new BigDecimal("33.33"));
        assertThat(response.getItems().get(1).getAmount()).isEqualByComparingTo(new BigDecimal("33.33"));
        assertThat(response.getItems().get(2).getAmount()).isEqualByComparingTo(new BigDecimal("33.34"));
    }

    @Test
    void getCardSummaryShouldUseBillingMonth() {
        when(transactionRepository.sumCardExpensesByBillingMonth(user.getId(), "2026-06"))
                .thenReturn(new BigDecimal("1320.00"));
        when(transactionRepository.countByUserIdAndTypeAndActiveTrueAndBillingMonth(user.getId(), TransactionType.EXPENSE, "2026-06"))
                .thenReturn(8L);

        CardSummaryResponse response = transactionService.getCardSummary("2026-06", user);

        assertThat(response.getBillingMonth()).isEqualTo("2026-06");
        assertThat(response.getTotal()).isEqualByComparingTo(new BigDecimal("1320.00"));
        assertThat(response.getTransactionsCount()).isEqualTo(8);
    }

    private CreateTransactionRequest createTransactionRequest(UUID accountId, UUID categoryId) {
        CreateTransactionRequest request = new CreateTransactionRequest();
        ReflectionTestUtils.setField(request, "description", "Mercado");
        ReflectionTestUtils.setField(request, "amount", new BigDecimal("50.00"));
        ReflectionTestUtils.setField(request, "type", TransactionType.EXPENSE);
        ReflectionTestUtils.setField(request, "occurredAt", LocalDate.of(2026, 5, 9));
        ReflectionTestUtils.setField(request, "accountId", accountId);
        ReflectionTestUtils.setField(request, "categoryId", categoryId);
        ReflectionTestUtils.setField(request, "note", "Compra semanal");
        return request;
    }

    private CreateTransferTransactionRequest createTransferTransactionRequest(UUID fromAccountId, UUID toAccountId) {
        CreateTransferTransactionRequest request = new CreateTransferTransactionRequest();
        ReflectionTestUtils.setField(request, "fromAccountId", fromAccountId);
        ReflectionTestUtils.setField(request, "toAccountId", toAccountId);
        ReflectionTestUtils.setField(request, "amount", new BigDecimal("200.00"));
        ReflectionTestUtils.setField(request, "occurredAt", LocalDate.of(2026, 5, 10));
        ReflectionTestUtils.setField(request, "description", "Reserva para investimento");
        ReflectionTestUtils.setField(request, "note", "Transferencia interna");
        return request;
    }

    private CreateInstallmentTransactionRequest createInstallmentRequest(
            UUID accountId,
            UUID categoryId,
            String totalAmount,
            int installmentTotal
    ) {
        CreateInstallmentTransactionRequest request = new CreateInstallmentTransactionRequest();
        ReflectionTestUtils.setField(request, "accountId", accountId);
        ReflectionTestUtils.setField(request, "categoryId", categoryId);
        ReflectionTestUtils.setField(request, "totalAmount", new BigDecimal(totalAmount));
        ReflectionTestUtils.setField(request, "installmentTotal", installmentTotal);
        ReflectionTestUtils.setField(request, "firstOccurredAt", LocalDate.of(2026, 5, 20));
        ReflectionTestUtils.setField(request, "firstBillingMonth", "2026-06");
        ReflectionTestUtils.setField(request, "description", "Compra parcelada");
        ReflectionTestUtils.setField(request, "note", "Parcelamento");
        return request;
    }

    private Specification<Transaction> anyTransactionSpecification() {
        return any();
    }

    private Pageable anyPageable() {
        return any();
    }
}
