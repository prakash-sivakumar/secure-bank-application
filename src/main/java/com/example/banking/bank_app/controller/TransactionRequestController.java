package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.*;
import com.example.banking.bank_app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value="/request")
public class TransactionRequestController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LogService logService;

    @Autowired
    private TransactionRequestService transactionRequestService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value="/list/{page}", method= RequestMethod.GET)
    public ModelAndView list(@PathVariable("page") int page, Authentication authentication, @ModelAttribute("message") String message) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        int critical;
        int role;
        if(roles.contains("TIER2")){
            critical = Config.CRITICAL_YES;
            role = Config.TIER2;
        }
        else{//tier 1
            critical = Config.CRITICAL_NO;
            role = Config.TIER1;
        }
        ModelAndView modelAndView = new ModelAndView("request_list");
        PageRequest pageable = PageRequest.of(page - 1, 10);
        Page<TransactionRequest> requestPage = transactionRequestService.getPaginated(pageable, critical);
        int totalPages = requestPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNums = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNums", pageNums);
        }
        modelAndView.addObject("message",message);
        modelAndView.addObject("activeRequestList", true);
        modelAndView.addObject("requestList", requestPage.getContent());
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @RequestMapping(value="/approve/{id}", method= RequestMethod.POST)
    public ModelAndView approve(@PathVariable("id") int id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Long userId =  employeeService.findUserByEmail(authentication.getName());
        Employee employee = employeeService.getEmployeeById(userId);
        TransactionRequest transactionRequest = transactionRequestService.getRequestByRequestId(new Long(id));
        transactionRequest.setApproved_at(new Timestamp(System.currentTimeMillis()));
        transactionRequest.setApproved_by(employee.getEmployee_name()); //Remeber to change this


        if(transactionRequest.getType() == Config.TRANSFER){
            Account from_account = accountService.getAccountByAccountNo(transactionRequest.getFrom_account());
            if(from_account.getBalance()-transactionRequest.getTransaction_amount() < 0){
                transactionRequest.setStatus_id(Config.DECLINED);
                transactionRequestService.saveOrUpdate(transactionRequest);
                redirectAttributes.addFlashAttribute("message", "Insufficient Balance, Declined transaction request");
                logService.saveLog(authentication.getName(),"Insufficient Balance, Declined transaction request for id:"+id);
                return new ModelAndView("redirect:/request/list/1");
            }
            from_account.setBalance(from_account.getBalance()-transactionRequest.getTransaction_amount());
            Account to_account = accountService.getAccountByAccountNo(transactionRequest.getTo_account());
            to_account.setBalance(to_account.getBalance()+transactionRequest.getTransaction_amount());
            accountService.saveOrUpdate(from_account);
            accountService.saveOrUpdate(to_account);

            List<Transaction> transactions = transactionService.findAllByRequest_id(transactionRequest.getRequest_id());
            for(int i=0;i<transactions.size();i++){
                if(transactions.get(i).getTransaction_type() == Config.DEBIT){
                    transactions.get(i).setBalance(from_account.getBalance());
                }else{
                    transactions.get(i).setBalance(to_account.getBalance());
                }
                transactions.get(i).setStatus(Config.APPROVED);
                transactionService.saveOrUpdate(transactions.get(i));
            }
        }
        else{
            Account account = accountService.getAccountByAccountNo(transactionRequest.getFrom_account());
            if(transactionRequest.getType() == Config.WITHDRAW){
                if(account.getBalance()-transactionRequest.getTransaction_amount() < 0){
                    transactionRequest.setStatus_id(Config.DECLINED);
                    transactionRequestService.saveOrUpdate(transactionRequest);
                    redirectAttributes.addFlashAttribute("message", "Insufficient Balance, Declined transaction request");
                    logService.saveLog(authentication.getName(),"Insufficient Balance, Declined transaction request for id:"+id);
                    return new ModelAndView("redirect:/request/list/1");
                }
                account.setBalance(account.getBalance()-transactionRequest.getTransaction_amount());
            }else{
                account.setBalance(account.getBalance()+transactionRequest.getTransaction_amount());
            }
            accountService.saveOrUpdate(account);

            List<Transaction> transactions = transactionService.findAllByRequest_id(transactionRequest.getRequest_id());
            for(int i=0;i<transactions.size();i++){
                transactions.get(i).setBalance(account.getBalance());
                transactions.get(i).setStatus(Config.APPROVED);
                transactionService.saveOrUpdate(transactions.get(i));
            }
        }
        transactionRequest.setStatus_id(Config.APPROVED);
        transactionRequestService.saveOrUpdate(transactionRequest);
        redirectAttributes.addFlashAttribute("message", "Approved transaction request");
        logService.saveLog(authentication.getName(),"Approved transaction request for id:"+id);
        return new ModelAndView("redirect:/request/list/1");
    }

    @RequestMapping(value="/decline/{id}", method= RequestMethod.POST)
    public ModelAndView decline(@PathVariable("id") int id, Authentication authentication) {
        Long userId =  employeeService.findUserByEmail(authentication.getName());
        Employee employee = employeeService.getEmployeeById(userId);
        TransactionRequest transactionRequest = transactionRequestService.getRequestByRequestId(new Long(id));
        transactionRequest.setApproved_at(new Timestamp(System.currentTimeMillis()));
        transactionRequest.setApproved_by(employee.getEmployee_name()); //Remeber to change this
        transactionRequest.setStatus_id(Config.DECLINED);
        transactionRequestService.saveOrUpdate(transactionRequest);
        logService.saveLog(authentication.getName(),"Declined transaction request for id:"+id);
        return new ModelAndView("redirect:/request/list/1");
    }

}