package com.eco.account.service;

import com.eco.account.dto.AccountResponse;
import com.eco.account.dto.CreateAccountRequest;
import com.eco.account.dto.UpdateAccountRequest;
import com.eco.account.model.Account;
import com.eco.account.model.AccountType;
import com.eco.account.repository.AccountRepository;
import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
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

    @InjectMocks
    private AccountService accountService;

    @Test
    void findAllShouldReturnAccounts() {
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO);

        when(accountRepository.findAll()).thenReturn(List.of(account));

        List<AccountResponse> response = accountService.findAll();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getName()).isEqualTo("Conta Principal");
        assertThat(response.getFirst().getType()).isEqualTo(AccountType.CHECKING);
    }

    @Test
    void findByIdShouldReturnAccountWhenExists() {
        UUID id = UUID.randomUUID();
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO);
        account.setId(id);

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.findById(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Conta Principal");
    }

    @Test
    void findByIdShouldThrowNotFoundWhenAccountDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Conta nao encontrada");
    }

    @Test
    void createShouldSaveAccountWhenNameDoesNotExist() {
        CreateAccountRequest request = createAccountRequest();

        when(accountRepository.existsByNameIgnoreCase("Conta Principal")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.create(request);

        assertThat(response.getName()).isEqualTo("Conta Principal");
        assertThat(response.getType()).isEqualTo(AccountType.CHECKING);
        assertThat(response.getInitialBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createShouldThrowBusinessExceptionWhenNameAlreadyExists() {
        CreateAccountRequest request = createAccountRequest();

        when(accountRepository.existsByNameIgnoreCase("Conta Principal")).thenReturn(true);

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Conta ja existe");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateShouldUpdateAccountWhenNameIsAvailable() {
        UUID id = UUID.randomUUID();
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO);
        UpdateAccountRequest request = updateAccountRequest();

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.findByNameIgnoreCase("Reserva")).thenReturn(Optional.empty());
        when(accountRepository.save(account)).thenReturn(account);

        AccountResponse response = accountService.update(id, request);

        assertThat(response.getName()).isEqualTo("Reserva");
        assertThat(response.getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(response.getInitialBalance()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void deactivateShouldSetAccountInactive() {
        UUID id = UUID.randomUUID();
        Account account = new Account("Conta Principal", AccountType.CHECKING, BigDecimal.ZERO);

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        accountService.deactivate(id);

        assertThat(account.isActive()).isFalse();
        verify(accountRepository).save(account);
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
