package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Transaction;
import com.example.banking.bank_app.respository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getAllTransactions() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    @Override
    public Transaction getTransactionByTransactionId(Long transferid) {
        return transactionRepository.findById(transferid).get();
    }

    @Override
    public void saveOrUpdate(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Long transferid) {
        transactionRepository.deleteById(transferid);
    }

    @Override
    public List<Transaction> findAllByAccountNo(Long account_no){
        return transactionRepository.findAllByAccountNo(account_no);
    }

    @Override
    public List<Transaction> findAllByRequest_id(Long request_id){
        return transactionRepository.findAllByRequest_id(request_id);
    }

}