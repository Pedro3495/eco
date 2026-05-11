package com.eco.account.controller;

import com.eco.account.dto.AccountBalanceResponse;
import com.eco.account.dto.AccountResponse;
import com.eco.account.dto.CreateAccountRequest;
import com.eco.account.dto.UpdateAccountRequest;
import com.eco.account.service.AccountService;
import com.eco.user.model.User;
import jakarta.validation.Valid;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> findAll(@AuthenticationPrincipal User user) {
        return accountService.findAll(user);
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return accountService.findById(id, user);
    }

    @GetMapping("/{id}/balance")
    public AccountBalanceResponse getBalance(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal User user
    ) {
        return accountService.getBalance(id, from, to, user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@RequestBody @Valid CreateAccountRequest request, @AuthenticationPrincipal User user) {
        return accountService.create(request, user);
    }

    @PutMapping("/{id}")
    public AccountResponse update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateAccountRequest request,
            @AuthenticationPrincipal User user
    ) {
        return accountService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        accountService.deactivate(id, user);
    }
}
