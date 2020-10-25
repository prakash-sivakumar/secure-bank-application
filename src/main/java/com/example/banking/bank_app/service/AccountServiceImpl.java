package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Account;
import com.example.banking.bank_app.respository.AccountRepository;
import com.example.banking.bank_app.respository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Override
    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepository.findAll();
    }

    @Override
    public Account getAccountByAccountNo(Long accountNo) {
        return accountRepository.findById(accountNo).get();
    }

    @Override
    public Account saveOrUpdate(Account account) {
//        Account test;
//        try{
//            getAccountByAccountNo(account.getAccountNo());
//        }
//        catch (Exception e){
//            test = accountRepository.save(account);
//            if(account.getAccountType() == Config.CREDITCARD) {
//                Card card = new Card();
//                card.setAccountNo(test.getAccountNo());
//                card.setBalance(account.getBalance());
//                card.setCreated(new Timestamp(System.currentTimeMillis()));
//                card.setCreditLimit(Config.DEFAULT_CREDIT_LIMIT);
//                card.setType(Config.CREDIT);
//                card.setUpdated(new Timestamp(System.currentTimeMillis()));
//                cardRepository.save(card);
//            }
//            return;
//        }
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long accountNo) {
        accountRepository.deleteById(accountNo);
    }


    @Override
    public Page<Account> getPaginated(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

}