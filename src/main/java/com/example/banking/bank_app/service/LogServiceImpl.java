package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Log;
import com.example.banking.bank_app.respository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Override
    public List<Log> getAllLogs() {
        return (List<Log>) logRepository.findAll();
    }

    @Override
    public Page<Log> getPaginated(Pageable pageable) {
        return logRepository.findAll(pageable);
    }

    @Override
    public void saveLog(String email, String message){
        Log log = new Log();
        log.setLogTimeStamp(new Timestamp(System.currentTimeMillis()));
        log.setMessage(message);
        log.setRelatedUserID(email);
        logRepository.save(log);
    }

}

