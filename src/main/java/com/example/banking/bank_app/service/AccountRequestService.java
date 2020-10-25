package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.AccountRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountRequestService {
    List<AccountRequest> getAllAccounts();

    AccountRequest getAccountByAccountNo(Long requestid);

    void saveOrUpdate(AccountRequest accountRequest);

    void deleteAccount(Long id);

    Page<AccountRequest> getPaginated(Pageable pageable, int role);

}
