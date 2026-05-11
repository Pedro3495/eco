package com.eco.account.service;

import com.eco.account.dto.AccountBalanceResponse;
import com.eco.account.dto.AccountResponse;
import com.eco.account.dto.CreateAccountRequest;
import com.eco.account.dto.UpdateAccountRequest;
import com.eco.account.model.Account;
import com.eco.account.repository.AccountRepository;
import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import com.eco.transaction.model.TransactionType;
import com.eco.transaction.repository.TransactionRepository;
import com.eco.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll(User user) {
        return accountRepository.findAllByUserId(user.getId())
                .stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(UUID id, User user) {
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));

        return AccountResponse.fromEntity(account);
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request, User user) {
        if (accountRepository.existsByNameIgnoreCaseAndUserId(request.getName(), user.getId())) {
            throw new BusinessException("Conta ja existe");
        }

        Account account = new Account(
                request.getName(),
                request.getType(),
                request.getInitialBalance(),
                user
        );

        Account savedAccount = accountRepository.save(account);

        return AccountResponse.fromEntity(savedAccount);
    }

    @Transactional
    public AccountResponse update(UUID id, UpdateAccountRequest request, User user) {
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));

        accountRepository.findByNameIgnoreCaseAndUserId(request.getName(), user.getId())
                .filter(existingAccount -> !existingAccount.getId().equals(id))
                .ifPresent(existingAccount -> {
                    throw new BusinessException("Conta ja existe");
                });

        account.setName(request.getName());
        account.setType(request.getType());
        account.setInitialBalance(request.getInitialBalance());
        account.setActive(request.getActive());

        Account updatedAccount = accountRepository.save(account);

        return AccountResponse.fromEntity(updatedAccount);
    }

    @Transactional
    public void deactivate(UUID id, User user) {
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));

        account.setActive(false);

        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public AccountBalanceResponse getBalance(UUID id, LocalDate from, LocalDate to, User user) {
        Account account = accountRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));

        BigDecimal income = transactionRepository.sumAmountByAccountAndTypeAndPeriod(
                user.getId(),
                id,
                TransactionType.INCOME,
                from,
                to
        );
        BigDecimal expense = transactionRepository.sumAmountByAccountAndTypeAndPeriod(
                user.getId(),
                id,
                TransactionType.EXPENSE,
                from,
                to
        );
        BigDecimal transferIn = transactionRepository.sumTransferInByAccountAndPeriod(user.getId(), id, from, to);
        BigDecimal transferOut = transactionRepository.sumTransferOutByAccountAndPeriod(user.getId(), id, from, to);
        BigDecimal balance = account.getInitialBalance()
                .add(income)
                .subtract(expense)
                .add(transferIn)
                .subtract(transferOut);

        return new AccountBalanceResponse(
                account.getId(),
                account.getInitialBalance(),
                income,
                expense,
                transferIn,
                transferOut,
                balance
        );
    }
}
