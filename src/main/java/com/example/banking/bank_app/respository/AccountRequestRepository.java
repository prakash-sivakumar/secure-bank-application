package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.AccountRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AccountRequestRepository extends CrudRepository<AccountRequest, Long> {

    @Query("SELECT t FROM AccountRequest t WHERE t.role = :role")
    Page<AccountRequest> findAll(Pageable pageable, @Param("role") int role);
}
