package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TransactionRequestRepository extends CrudRepository<TransactionRequest, Long> {

    @Query("SELECT t FROM TransactionRequest t WHERE t.critical = :critical")
    Page<TransactionRequest> findAll(Pageable pageable, @Param("critical") int critical);

}


