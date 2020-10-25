package com.example.banking.bank_app.respository;
import com.example.banking.bank_app.model.Help;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface HelpRepository extends CrudRepository<Help, Long> {

    Page<Help> findAll(Pageable pageable);
}

