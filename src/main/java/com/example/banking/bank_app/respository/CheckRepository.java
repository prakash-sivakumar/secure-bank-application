package com.example.banking.bank_app.respository;
import com.example.banking.bank_app.model.Check;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CheckRepository extends CrudRepository<Check, Long>  {

    Page<Check> findAll(Pageable pageable);
}
