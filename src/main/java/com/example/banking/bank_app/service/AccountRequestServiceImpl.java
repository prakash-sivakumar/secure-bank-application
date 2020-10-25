package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.AccountRequest;
import com.example.banking.bank_app.respository.AccountRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class AccountRequestServiceImpl implements AccountRequestService {

    @Autowired
    private AccountRequestRepository accountRequestRepository;

    @Override
    public List<AccountRequest> getAllAccounts() {
        return (List<AccountRequest>) accountRequestRepository.findAll();
    }

    @Override
    public AccountRequest getAccountByAccountNo(Long accountNo) {
        return accountRequestRepository.findById(accountNo).get();
    }

    @Override
    public void saveOrUpdate(AccountRequest accountRequest) {

        accountRequestRepository.save(accountRequest);
    }

    @Override
    public void deleteAccount(Long accountNo) {
        accountRequestRepository.deleteById(accountNo);
    }

    @Override
    public Page<AccountRequest> getPaginated(Pageable pageable, int role) {
        return accountRequestRepository.findAll(pageable, role);
    }


}