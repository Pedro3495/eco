package com.eco.account.service;

import com.eco.account.dto.AccountResponse;
import com.eco.account.dto.CreateAccountRequest;
import com.eco.account.dto.UpdateAccountRequest;
import com.eco.account.model.Account;
import com.eco.account.repository.AccountRepository;
import com.eco.common.exception.BusinessException;
import com.eco.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));

        return AccountResponse.fromEntity(account);
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        if (accountRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("Conta ja existe");
        }

        Account account = new Account(
                request.getName(),
                request.getType(),
                request.getInitialBalance()
        );

        Account savedAccount = accountRepository.save(account);

        return AccountResponse.fromEntity(savedAccount);
    }

    @Transactional
    public AccountResponse update(UUID id, UpdateAccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));

        accountRepository.findByNameIgnoreCase(request.getName())
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
    public void deactivate(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada"));

        account.setActive(false);

        accountRepository.save(account);
    }
}
