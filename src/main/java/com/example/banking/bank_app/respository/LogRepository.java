package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;



public interface LogRepository extends CrudRepository<Log, Integer> {

Page<Log> findAll(Pageable pageable);

}
