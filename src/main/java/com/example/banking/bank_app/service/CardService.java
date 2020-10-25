package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Card;

import java.util.List;

public interface CardService {
    List<Card> getAllCards();

    Card getCardByCardId(Long cardId);

    void saveOrUpdate(Card card);

    void deleteCard(Long id);
}
