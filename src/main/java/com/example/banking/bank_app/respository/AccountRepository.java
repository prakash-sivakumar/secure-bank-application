package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Page<Account> findAll(Pageable pageable);
}
