package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Check;
import com.example.banking.bank_app.respository.CheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckServiceImpl implements CheckService {

    @Autowired
    private CheckRepository checkRepository;

    @Override
    public List<Check> getAllChecks() {
        return (List<Check>) checkRepository.findAll();
    }

    @Override
    public Check getCheckByAccountNo(Long accountNo) {
        return checkRepository.findById(accountNo).get();
    }

    @Override
    public void saveOrUpdate(Check check) {
        checkRepository.save(check);
    }

    @Override
    public void deleteCheck(Long checkId) {
        checkRepository.deleteById(checkId);
    }


    @Override
    public Page<Check> getPaginated(Pageable pageable) {
        return checkRepository.findAll(pageable);
    }
}
