package com.example.banking.bank_app.service;
import com.example.banking.bank_app.model.Check;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CheckService {
    List<Check> getAllChecks();

    Check getCheckByAccountNo(Long accountNo);

    void saveOrUpdate(Check check);

    void deleteCheck(Long id);

    Page<Check> getPaginated(Pageable pageable);

}
