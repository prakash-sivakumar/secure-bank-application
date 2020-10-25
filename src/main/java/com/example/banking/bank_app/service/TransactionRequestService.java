package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionRequestService {
    List<TransactionRequest> getAllRequests();

    TransactionRequest getRequestByRequestId(Long request_id);

    TransactionRequest saveOrUpdate(TransactionRequest request);

    void deleteRequest(Long request_id);

    Page<TransactionRequest> getPaginated(Pageable pageable, int critical);

}
