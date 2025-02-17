package org.example.final_graduation.services;

import org.example.final_graduation.entities.Account;
import org.example.final_graduation.entities.Authority;
import org.example.final_graduation.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Authority> getAllCustomers() {
        return accountRepository.findAllCustomers();
    }

    public List<Account> getAllAccountsByRoles() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Integer id) {
        return accountRepository.findById(id);
    }
}
