package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.Account;
import com.example.banking.bank_app.model.Check;
import com.example.banking.bank_app.model.Config;
import com.example.banking.bank_app.model.Transaction;
import com.example.banking.bank_app.service.AccountService;
import com.example.banking.bank_app.service.CheckService;
import com.example.banking.bank_app.service.LogService;
import com.example.banking.bank_app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value="/checks")
public class CheckController {

    @Autowired
    private CheckService checkService;

    @Autowired
    private LogService logService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value="/list/{page}", method= RequestMethod.GET)
    public ModelAndView list(@PathVariable("page") int page) {
        ModelAndView modelAndView = new ModelAndView("check_list");
        PageRequest pageable = PageRequest.of(page - 1, 15);
        Page<Check> checkPage = checkService.getPaginated(pageable);
        int totalPages = checkPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("activeCheckList", true);
        modelAndView.addObject("checkList", checkPage.getContent());
        return modelAndView;
    }

    @RequestMapping(value = "/issue", method = RequestMethod.GET)
    public ModelAndView AddUserForm(@ModelAttribute("message") String message) {
        ModelAndView modelAndView = new ModelAndView();
        Check check = new Check();
        modelAndView.addObject("check", check);
        modelAndView.setViewName("issueCheck"); // resources/template/register.html
        modelAndView.addObject("message",message);
        return modelAndView;
    }

    @RequestMapping(value="/issue", method= RequestMethod.POST)
    public ModelAndView issue(Check check, RedirectAttributes redirectAttributes, Authentication authentication) {
        Account account;
        try{
            account = accountService.getAccountByAccountNo(check.getAccountno());
        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("message", "Account Number doesn't Exist!");
            return new ModelAndView("redirect:/checks/issue");
        }
        if(check.getAmount() < 0){
            redirectAttributes.addFlashAttribute("message", "Amount cannot be negative!");
            return new ModelAndView("redirect:/checks/issue");
        }
        if(account.getBalance() - check.getAmount() < 0){
            redirectAttributes.addFlashAttribute("message", "No Sufficient Balance");
            return new ModelAndView("redirect:/checks/issue");
        }
        check.setIssued_at(new Timestamp(System.currentTimeMillis()));
        checkService.saveOrUpdate(check);
        Transaction transaction = new Transaction();
        transaction.setTransaction_amount(check.getAmount());
        transaction.setTransaction_timestamp(new Timestamp(System.currentTimeMillis()));
        transaction.setTransaction_type(Config.DEBIT);
        transaction.setDescription("Issued check for :" + check.getAmount());
        transaction.setStatus(Config.APPROVED);
        transaction.setAccount_no(check.getAccountno());
        transaction.setBalance(account.getBalance() - check.getAmount());
        transactionService.saveOrUpdate(transaction);
        account.setBalance(account.getBalance() - check.getAmount());
        accountService.saveOrUpdate(account);
        logService.saveLog(authentication.getName(),"Issued check for "+check.getAccountno()+" of amount $"+check.getAmount());
        return new ModelAndView("redirect:/checks/list/1");
    }
}
