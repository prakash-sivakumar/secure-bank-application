package com.example.banking.bank_app.controller;


import com.example.banking.bank_app.model.Account;
import com.example.banking.bank_app.model.Transaction;
import com.example.banking.bank_app.service.TransactionService;
import com.example.banking.bank_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value="/statement/list", method= RequestMethod.GET)
    public List<Transaction> statement_list(Authentication authentication) {
        Long userId =  userService.findUserByEmail(authentication.getName());
        List<Transaction> transactions= new ArrayList<>();
        for(Account account: userService.getUserByUserId(userId).getAccounts()){
            transactions.addAll(transactionService.findAllByAccountNo(account.getAccountNo()));
        }
        return transactions;
    }

    @RequestMapping(value="/statements", method= RequestMethod.GET)
    public ModelAndView statements() {
        return new ModelAndView("statement_list");
    }

}
