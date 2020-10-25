package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.Card;
import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Long> {

}
