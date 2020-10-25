package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long>{

    @Query("SELECT t FROM Transaction t WHERE t.account_no = :account_no")
    List<Transaction> findAllByAccountNo(@Param("account_no") Long account_no);

    @Query("SELECT t FROM Transaction t WHERE t.request_id = :request_id")
    List<Transaction> findAllByRequest_id(@Param("request_id") Long request_id);
}