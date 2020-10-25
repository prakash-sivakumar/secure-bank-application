package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();

    Account getAccountByAccountNo(Long accountNo);

    Account saveOrUpdate(Account account);

    void deleteAccount(Long id);

    Page<Account> getPaginated(Pageable pageable);

}
