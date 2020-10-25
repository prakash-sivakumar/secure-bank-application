package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.*;
import com.example.banking.bank_app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class TransferController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LogService logService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRequestService transactionRequestService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private OtpService otpService;

    @GetMapping("/transfer/{type}")
    public String transactionForm(Model model, @PathVariable String type, Authentication authentication, @ModelAttribute("message") String message) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        int role;
        if(roles.contains("TIER1")){
            role = Config.TIER1;
        }else{
            Long id =  userService.findUserByEmail(authentication.getName());
            role = Config.USER;
            model.addAttribute("accounts",userService.getUserByUserId(id).getAccounts());
        }
        Transfer transfer = new Transfer();
        model.addAttribute("transfer", transfer);
        model.addAttribute("type", type);
        model.addAttribute("message",message);
        model.addAttribute("role", role);
        return "transaction";
    }

    @RequestMapping(value = "/transfer/{type}", method= RequestMethod.POST)
    public String formSubmit(@Valid Transfer transfer,  BindingResult bindingResult, @PathVariable String type,  Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please correct the errors!");
            return "redirect:/transfer/"+type;
        }
        if(!otpService.validateOtp(transfer.getOtp(), authentication.getName())){
            redirectAttributes.addFlashAttribute("message","Invalid OTP!");
            return "redirect:/transfer/"+type;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        if(roles.contains("USER")){
            User user = userService.getUserByUserId(userService.findUserByEmail(authentication.getName()));
            boolean flag = false;
            for(Account account: user.getAccounts()){
                if(transfer.getFrom_account_no().equals(account.getAccountNo())){
                    flag = true;
                }
            }
            if(!flag){
                return "No authorization for this Account!";
            }
        }
        Account from_account;
        Account to_account;
        try{
            to_account = accountService.getAccountByAccountNo(transfer.getTo_account_no());
            from_account = accountService.getAccountByAccountNo(transfer.getFrom_account_no());
            if(from_account.getAccountNo().equals(to_account.getAccountNo())){
                redirectAttributes.addFlashAttribute("message","Account numbers cannot be same!");
                return "redirect:/transfer/"+type;
            }
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("message","Account number doesn't exists!");
            return "redirect:/transfer/"+type;
        }
        if(transfer.getTransaction_amount() < 0){
            redirectAttributes.addFlashAttribute("message","Amount cannot be negative!");
            return "redirect:/transfer/"+type;
        }
        if(from_account.getBalance() - transfer.getTransaction_amount() < 0){
            redirectAttributes.addFlashAttribute("message","Insufficient balance!");
            return "redirect:/transfer/"+type;
        }
        //Dont delete the comment
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        List<String> roles = new ArrayList<String>();
//        for(GrantedAuthority a : authorities) {
//            roles.add(a.getAuthority());
//        }
        String name;
        if(roles.contains("TIER1")){
            Long id = employeeService.findUserByEmail(authentication.getName());
            name = employeeService.getEmployeeById(id).getEmployee_name();
        }else{
            Long id = userService.findUserByEmail(authentication.getName());
            name = userService.getUserByUserId(id).getName();
        }
        Transaction from_transaction = new Transaction();
        Transaction to_transaction = new Transaction();
        from_transaction.setTransaction_amount(transfer.getTransaction_amount());
        from_transaction.setTransaction_timestamp(new Timestamp(System.currentTimeMillis()));
        from_transaction.setTransaction_type(Config.DEBIT);
        from_transaction.setDescription("Transfer to "+transfer.getTo_account_no()+ " || Comments: "+transfer.getDescription());
        from_transaction.setAccount_no(transfer.getFrom_account_no());
        from_transaction.setStatus(Config.APPROVED);
        float from_balance = accountService.getAccountByAccountNo(transfer.getFrom_account_no()).getBalance();
        from_transaction.setBalance(from_balance - transfer.getTransaction_amount());

        to_transaction.setTransaction_amount(transfer.getTransaction_amount());
        to_transaction.setTransaction_timestamp(new Timestamp(System.currentTimeMillis()));
        to_transaction.setTransaction_type(Config.CREDIT);
        to_transaction.setDescription("Transfer from "+transfer.getFrom_account_no()+ " || Comments: "+transfer.getDescription());
        to_transaction.setAccount_no(transfer.getTo_account_no());
        to_transaction.setStatus(Config.APPROVED);
        float to_balance = accountService.getAccountByAccountNo(transfer.getTo_account_no()).getBalance();
        to_transaction.setBalance(to_balance+transfer.getTransaction_amount());

        Long request_id = 0L;
        if(transfer.getTransaction_amount() > Config.LIMIT || roles.contains("USER")){
            TransactionRequest transactionRequest = new TransactionRequest();
            transactionRequest.setCreated_by(name);
            transactionRequest.setFrom_account(transfer.getFrom_account_no());
            transactionRequest.setTo_account(transfer.getTo_account_no());
            transactionRequest.setStatus_id(Config.PENDING);
            transactionRequest.setTransaction_amount(transfer.getTransaction_amount());
            transactionRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));
            transactionRequest.setDescription("Fund Transfer || Comments: "+transfer.getDescription());
            transactionRequest.setType(Config.TRANSFER);
            if (transfer.getTransaction_amount() > Config.LIMIT){
                transactionRequest.setCritical(Config.CRITICAL_YES);
            }else{
                transactionRequest.setCritical(Config.CRITICAL_NO);
            }
            request_id = transactionRequestService.saveOrUpdate(transactionRequest).getRequest_id();
            from_transaction.setRequest_id(request_id);
            from_transaction.setStatus(Config.PENDING);
            from_transaction.setBalance(from_balance);

            to_transaction.setRequest_id(request_id);
            to_transaction.setStatus(Config.PENDING);
            to_transaction.setBalance(to_balance);
        }else{
            from_account.setBalance(from_balance-transfer.getTransaction_amount());

            to_account.setBalance(to_balance+transfer.getTransaction_amount());

            accountService.saveOrUpdate(from_account);
            accountService.saveOrUpdate(to_account);
        }
        transactionService.saveOrUpdate(from_transaction);
        transactionService.saveOrUpdate(to_transaction);
        if(transfer.getTransaction_amount() > Config.LIMIT || roles.contains("USER")){
            redirectAttributes.addFlashAttribute("message","Successfully transferred, pending for approval!");
        }else{
            redirectAttributes.addFlashAttribute("message","Successfully transferred");
        }

        logService.saveLog(authentication.getName(),"Transferred money from "+transfer.getFrom_account_no()+" to "+transfer.getTo_account_no()+ " for $"+transfer.getTransaction_amount());
        return "redirect:/transfer/"+type;
    }
}
