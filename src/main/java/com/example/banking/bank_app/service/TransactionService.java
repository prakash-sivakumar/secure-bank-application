package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Transaction;

import java.util.List;
public interface TransactionService {
    List<Transaction> getAllTransactions();

    Transaction getTransactionByTransactionId(Long transfer_id);

    void saveOrUpdate(Transaction transfer_id);

    void deleteTransaction(Long transfer_id);

    List<Transaction> findAllByAccountNo(Long account_no);

    List<Transaction> findAllByRequest_id(Long request_id);
}