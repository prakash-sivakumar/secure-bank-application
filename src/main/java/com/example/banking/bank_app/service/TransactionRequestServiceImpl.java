package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.TransactionRequest;
import com.example.banking.bank_app.respository.TransactionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionRequestServiceImpl implements TransactionRequestService {

    @Autowired
    private TransactionRequestRepository transactionRequestRepository;

    @Override
    public List<TransactionRequest> getAllRequests() {
        return (List<TransactionRequest>) transactionRequestRepository.findAll();
    }

    @Override
    public TransactionRequest getRequestByRequestId(Long request_id) {
        return transactionRequestRepository.findById(request_id).get();
    }

    @Override
    public TransactionRequest saveOrUpdate(TransactionRequest request) {
        return transactionRequestRepository.save(request);
    }

    @Override
    public void deleteRequest(Long request_id) {
        transactionRequestRepository.deleteById(request_id);
    }

    @Override
    public Page<TransactionRequest> getPaginated(Pageable pageable, int critical) {
        return transactionRequestRepository.findAll(pageable, critical);
    }

}