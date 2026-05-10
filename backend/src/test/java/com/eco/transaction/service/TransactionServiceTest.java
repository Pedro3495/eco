package com.eco.transaction.service;

import com.eco.account.model.Account;
import com.eco.account.model.AccountType;
import com.eco.account.repository.AccountRepository;
import com.eco.category.model.Category;
import com.eco.category.model.CategoryKind;
import com.eco.category.repository.CategoryRepository;
import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import com.eco.transaction.dto.CreateTransactionRequest;
import com.eco.transaction.dto.TransactionPageResponse;
import com.eco.transaction.dto.TransactionResponse;
import com.eco.transaction.model.Transaction;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
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

    @Test
    void findAllShouldReturnTransactions() {
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO);
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils");
        Transaction transaction = new Transaction(
                "Mercado",
                new BigDecimal("50.00"),
                TransactionType.EXPENSE,
                LocalDate.of(2026, 5, 9),
                account,
                category,
                "Compra semanal"
        );

        Pageable pageable = PageRequest.of(0, 10);

        when(transactionRepository.findAll(anyTransactionSpecification(), anyPageable()))
                .thenReturn(new PageImpl<>(List.of(transaction), pageable, 1));

        TransactionPageResponse response = transactionService.findAll(null, null, null, null, null, null, pageable);

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
                pageable
        );

        assertThat(response.getItems()).isEmpty();
        verify(transactionRepository).findAll(anyTransactionSpecification(), anyPageable());
    }

    @Test
    void findByIdShouldThrowNotFoundWhenTransactionDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Transacao nao encontrada");
    }

    @Test
    void createShouldSaveTransactionWhenRelationsExist() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO);
        Category category = new Category("Alimentacao", CategoryKind.EXPENSE, "#E86F51", "utensils");
        account.setId(accountId);
        category.setId(categoryId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.create(request);

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
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO);
        Category category = new Category("Salario", CategoryKind.INCOME, "#2E8B57", "wallet");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> transactionService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Categoria incompativel com o tipo da transacao");
    }

    @Test
    void createShouldAcceptBothCategoryForAnyTransactionType() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO);
        Category category = new Category("Ajustes", CategoryKind.BOTH, "#64748B", "repeat");
        account.setId(accountId);
        category.setId(categoryId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.create(request);

        assertThat(response.getCategoryId()).isEqualTo(categoryId);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createShouldThrowNotFoundWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CreateTransactionRequest request = createTransactionRequest(accountId, categoryId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.create(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Conta nao encontrada");
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

    private Specification<Transaction> anyTransactionSpecification() {
        return any();
    }

    private Pageable anyPageable() {
        return any();
    }
}
