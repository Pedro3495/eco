package com.eco.account.service;

import com.eco.account.dto.AccountBalanceResponse;
import com.eco.account.dto.AccountResponse;
import com.eco.account.dto.CreateAccountRequest;
import com.eco.account.dto.UpdateAccountRequest;
import com.eco.account.model.Account;
import com.eco.account.model.AccountType;
import com.eco.account.repository.AccountRepository;
import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private final User user = new User("Usuario Dev", "dev@eco.com", "hash");

    @Test
    void findAllShouldReturnAccounts() {
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);

        when(accountRepository.findAllByUserId(user.getId())).thenReturn(List.of(account));

        List<AccountResponse> response = accountService.findAll(user);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getName()).isEqualTo("Conta Principal");
        assertThat(response.getFirst().getType()).isEqualTo(AccountType.CHECKING);
    }

    @Test
    void findByIdShouldReturnAccountWhenExists() {
        UUID id = UUID.randomUUID();
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        account.setId(id);

        when(accountRepository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(account));

        AccountResponse response = accountService.findById(id, user);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Conta Principal");
    }

    @Test
    void findByIdShouldThrowNotFoundWhenAccountDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(accountRepository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(id, user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Conta nao encontrada");
    }

    @Test
    void createShouldSaveAccountWhenNameDoesNotExist() {
        CreateAccountRequest request = createAccountRequest();

        when(accountRepository.existsByNameIgnoreCaseAndUserId("Conta Principal", user.getId())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.create(request, user);

        assertThat(response.getName()).isEqualTo("Conta Principal");
        assertThat(response.getType()).isEqualTo(AccountType.CHECKING);
        assertThat(response.getInitialBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createShouldThrowBusinessExceptionWhenNameAlreadyExists() {
        CreateAccountRequest request = createAccountRequest();

        when(accountRepository.existsByNameIgnoreCaseAndUserId("Conta Principal", user.getId())).thenReturn(true);

        assertThatThrownBy(() -> accountService.create(request, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Conta ja existe");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateShouldUpdateAccountWhenNameIsAvailable() {
        UUID id = UUID.randomUUID();
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);
        UpdateAccountRequest request = updateAccountRequest();

        when(accountRepository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(account));
        when(accountRepository.findByNameIgnoreCaseAndUserId("Reserva", user.getId())).thenReturn(Optional.empty());
        when(accountRepository.save(account)).thenReturn(account);

        AccountResponse response = accountService.update(id, request, user);

        assertThat(response.getName()).isEqualTo("Reserva");
        assertThat(response.getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(response.getInitialBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void deactivateShouldSetAccountInactive() {
        UUID id = UUID.randomUUID();
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO, user);

        when(accountRepository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(account));

        accountService.deactivate(id, user);

        assertThat(account.isActive()).isFalse();
        verify(accountRepository).save(account);
    }

    @Test
    void getBalanceShouldCalculateOriginAccountBalanceWithTransferOut() {
        UUID id = UUID.randomUUID();
        LocalDate from = LocalDate.of(2026, 5, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        Account account = new Account("Conta Principal", AccountType.CHECKING, new BigDecimal("1000.00"), user);
        account.setId(id);

        when(accountRepository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(account));
        when(transactionRepository.sumAmountByAccountAndTypeAndPeriod(user.getId(), id, TransactionType.INCOME, from, to))
                .thenReturn(new BigDecimal("3000.00"));
        when(transactionRepository.sumAmountByAccountAndTypeAndPeriod(user.getId(), id, TransactionType.EXPENSE, from, to))
                .thenReturn(new BigDecimal("1200.50"));
        when(transactionRepository.sumTransferInByAccountAndPeriod(user.getId(), id, from, to))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumTransferOutByAccountAndPeriod(user.getId(), id, from, to))
                .thenReturn(new BigDecimal("300.00"));

        AccountBalanceResponse response = accountService.getBalance(id, from, to, user);

        assertThat(response.getAccountId()).isEqualTo(id);
        assertThat(response.getInitialBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(response.getIncome()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(response.getExpense()).isEqualByComparingTo(new BigDecimal("1200.50"));
        assertThat(response.getTransferIn()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getTransferOut()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("2499.50"));
    }

    @Test
    void getBalanceShouldCalculateDestinationAccountBalanceWithTransferIn() {
        UUID id = UUID.randomUUID();
        LocalDate from = LocalDate.of(2026, 5, 1);
        LocalDate to = LocalDate.of(2026, 5, 31);
        Account account = new Account("Reserva", AccountType.SAVINGS, new BigDecimal("500.00"), user);
        account.setId(id);

        when(accountRepository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(account));
        when(transactionRepository.sumAmountByAccountAndTypeAndPeriod(user.getId(), id, TransactionType.INCOME, from, to))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumAmountByAccountAndTypeAndPeriod(user.getId(), id, TransactionType.EXPENSE, from, to))
                .thenReturn(new BigDecimal("50.00"));
        when(transactionRepository.sumTransferInByAccountAndPeriod(user.getId(), id, from, to))
                .thenReturn(new BigDecimal("300.00"));
        when(transactionRepository.sumTransferOutByAccountAndPeriod(user.getId(), id, from, to))
                .thenReturn(BigDecimal.ZERO);

        AccountBalanceResponse response = accountService.getBalance(id, from, to, user);

        assertThat(response.getTransferIn()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(response.getTransferOut()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("750.00"));
    }

    private CreateAccountRequest createAccountRequest() {
        CreateAccountRequest request = new CreateAccountRequest();
        ReflectionTestUtils.setField(request, "name", "Conta Principal");
        ReflectionTestUtils.setField(request, "type", AccountType.CHECKING);
        ReflectionTestUtils.setField(request, "initialBalance", BigDecimal.ZERO);
        return request;
    }

    private UpdateAccountRequest updateAccountRequest() {
        UpdateAccountRequest request = new UpdateAccountRequest();
        ReflectionTestUtils.setField(request, "name", "Reserva");
        ReflectionTestUtils.setField(request, "type", AccountType.SAVINGS);
        ReflectionTestUtils.setField(request, "initialBalance", new BigDecimal("100.00"));
        ReflectionTestUtils.setField(request, "active", true);
        return request;
    }
}
