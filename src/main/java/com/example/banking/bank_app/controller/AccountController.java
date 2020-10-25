package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.*;
import com.example.banking.bank_app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value="/account")
public class  AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private LogService logService;

    @Autowired
    private AccountRequestService accountRequestService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRequestService transactionRequestService;

    @RequestMapping(value="/list/{page}", method= RequestMethod.GET)
    public ModelAndView list(@PathVariable("page") int page, Authentication authentication, @ModelAttribute("message") String message) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        ModelAndView modelAndView;
        if(roles.contains("TIER1")){
            modelAndView = new ModelAndView("account_list");
        }else{
            modelAndView = new ModelAndView("account_list_tier2");
        }

        PageRequest pageable = PageRequest.of(page - 1, 15);
        Page<Account> accountPage = accountService.getPaginated(pageable);
        int totalPages = accountPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("accountList", accountPage.getContent());
        Account account = new Account();
        modelAndView.addObject("account", account);
        modelAndView.addObject("message", message);
        return modelAndView;
    }

    @RequestMapping(value="/new", method= RequestMethod.POST)
    public ModelAndView createAccount(Account account, RedirectAttributes redirectAttributes, Authentication authentication) {
        if(account.getAccountType() < Config.CHECKINGS || account.getAccountType() > Config.CREDITCARD){
            redirectAttributes.addFlashAttribute("message", "Please select appropriate account type!");
            return new ModelAndView("redirect:/user");
        }
        Long id =  userService.findUserByEmail(authentication.getName());
        String name = userService.getUserByUserId(id).getName();
        redirectAttributes.addFlashAttribute("message", "Created account, pending with Bank authorities!");
        SecureRandom random = new SecureRandom();
        int routing = random.nextInt(100000);
        AccountRequest accountRequest = new AccountRequest();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("account_no",null);
        attributes.put("user_id", id);
        attributes.put("balance", 0);
        attributes.put("account_type", account.getAccountType());
        attributes.put("routing_no", routing);
        attributes.put("interest",Config.DEFAULT_INTEREST);
        accountRequest.setDescription("New Account for "+name+" : " +authentication.getName());
        accountRequest.setAccount(attributes);
        accountRequest.setCreated_by(name);
        accountRequest.setStatus_id(Config.PENDING);
        accountRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));
        accountRequest.setType(Config.ACCOUNT_TYPE);
        accountRequest.setRole(Config.TIER2);
        try {
            accountRequest.serializeaccount();
        }
        catch(Exception e){
            System.out.println("Exception");
        }
        accountRequestService.saveOrUpdate(accountRequest);
        logService.saveLog(authentication.getName(), "Created new account for "+authentication.getName());
        return new ModelAndView("redirect:/user");
    }

    @RequestMapping(value="/get/{account_no}", method= RequestMethod.GET)
    public Account getAccount(@PathVariable("account_no") Long account_no) {
        return accountService.getAccountByAccountNo(account_no);
    }

    @RequestMapping(value="/edit", method= RequestMethod.POST)
    public ModelAndView editAccount(@Valid Account account, BindingResult bindingResult, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return new ModelAndView("redirect:/account/list/1");
        }
        Account oldAccount = null;
        try{
            oldAccount =accountService.getAccountByAccountNo(account.getAccountNo());
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("message", "Account number & Name cannot be edited");
            return new ModelAndView("redirect:/account/list/1");
        }
        if(!account.getAccountNo().equals(oldAccount.getAccountNo())){
            redirectAttributes.addFlashAttribute("message", "Account number & Name cannot be edited");
            return new ModelAndView("redirect:/account/list/1");
        }
        if(!account.getUserName().equals(oldAccount.getUserName())){
            redirectAttributes.addFlashAttribute("message", "Account number & Name cannot be edited");
            return new ModelAndView("redirect:/account/list/1");
        }
        if(oldAccount.getBalance() != account.getBalance()){
            redirectAttributes.addFlashAttribute("message", "Balance cannot be edited");
            return new ModelAndView("redirect:/account/list/1");
        }
        if(account.getBalance() < 0){
            redirectAttributes.addFlashAttribute("message", "Balance cannot be negative");
            return new ModelAndView("redirect:/account/list/1");
        }
        if(account.getInterest() < 0){
            redirectAttributes.addFlashAttribute("message", "Interest cannot be negative");
            return new ModelAndView("redirect:/account/list/1");
        }
        if(account.getAccountType() <0 || account.getAccountType() > 3){
            redirectAttributes.addFlashAttribute("message", "Account type is not correct");
            return new ModelAndView("redirect:/account/list/1");
        }
        account.setCreated(oldAccount.getCreated());
        account.setRoutingNo(oldAccount.getRoutingNo());
        account.setUserId(oldAccount.getUserId());
        account.setUpdated(new Timestamp(System.currentTimeMillis()));
        accountService.saveOrUpdate(account);
        logService.saveLog(authentication.getName(), "Account edit for  "+account.getAccountNo());
        redirectAttributes.addFlashAttribute("message", "Successfully saved");
        return new ModelAndView("redirect:/account/list/1");
    }

    @RequestMapping(value="/delete/{account_no}", method= RequestMethod.POST)
    public ModelAndView deleteAccount(@PathVariable("account_no") Long account_no) {
        accountService.deleteAccount(account_no);
        return new ModelAndView("redirect:/account/list/1");
    }

    @RequestMapping(value="/deposit", method= RequestMethod.GET)//tier 1
    public ModelAndView deposit(@ModelAttribute("message") String message) {
        ModelAndView modelAndView = new ModelAndView("deposit");
        Transaction transaction = new Transaction();
        modelAndView.addObject("transaction", transaction);
        modelAndView.addObject("message",message);
        return modelAndView;
    }

    @RequestMapping(value="/deposit1", method= RequestMethod.GET) //user
    public ModelAndView deposit1(@ModelAttribute("message") String message, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("deposit_user");
        Transaction transaction = new Transaction();
        modelAndView.addObject("transaction", transaction);
        modelAndView.addObject("message",message);
        Long id =  userService.findUserByEmail(authentication.getName());
        modelAndView.addObject("accounts",userService.getUserByUserId(id).getAccounts());
        return modelAndView;
    }

    @RequestMapping(value="/withdraw", method= RequestMethod.GET)//tier 1
    public ModelAndView withdraw(@ModelAttribute("message") String message) {
        ModelAndView modelAndView = new ModelAndView("withdraw");
        Transaction transaction = new Transaction();
        modelAndView.addObject("transaction", transaction);
        modelAndView.addObject("message",message);
        return modelAndView;
    }

    @RequestMapping(value="/withdraw1", method= RequestMethod.GET) //user
    public ModelAndView withdraw1(@ModelAttribute("message") String message, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("withdraw_user");
        Transaction transaction = new Transaction();
        modelAndView.addObject("transaction", transaction);
        modelAndView.addObject("message",message);
        Long id =  userService.findUserByEmail(authentication.getName());
        modelAndView.addObject("accounts",userService.getUserByUserId(id).getAccounts());
        return modelAndView;
    }

    @RequestMapping(value="/deposit", method= RequestMethod.POST)
    public ModelAndView depositPost(@Valid Transaction transaction, BindingResult bindingResult,Authentication authentication,  RedirectAttributes redirectAttributes) {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        String name;
        int role;
        ModelAndView modelAndView;
        if(roles.contains("TIER1")){
            Long id = employeeService.findUserByEmail(authentication.getName());
            name = employeeService.getEmployeeById(id).getEmployee_name();
            role = Config.TIER1;
            modelAndView = new ModelAndView("redirect:/account/deposit");
        }else{
            Long id = userService.findUserByEmail(authentication.getName());
            name = userService.getUserByUserId(id).getName();
            role = Config.USER;
            modelAndView = new ModelAndView("redirect:/account/deposit1");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return modelAndView;
        }
        String message = depositandwithdraw(Config.CREDIT, transaction,name, role, authentication);
        if(message.contains("Success")){
            logService.saveLog(authentication.getName(), "Deposited money for account "+transaction.getAccount_no()+" $"+transaction.getTransaction_amount());
        }
        redirectAttributes.addFlashAttribute("message", message);
        return modelAndView;
    }

    @RequestMapping(value="/withdraw", method= RequestMethod.POST)
    public ModelAndView withdrawPost(@Valid Transaction transaction,BindingResult bindingResult,Authentication authentication,  RedirectAttributes redirectAttributes) {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        String name;
        int role;
        ModelAndView modelAndView;
        if(roles.contains("TIER1")){
            Long id = employeeService.findUserByEmail(authentication.getName());
            name = employeeService.getEmployeeById(id).getEmployee_name();
            role = Config.TIER1;
            modelAndView = new ModelAndView("redirect:/account/withdraw");
        }else{
            Long id = userService.findUserByEmail(authentication.getName());
            name = userService.getUserByUserId(id).getName();
            role = Config.USER;
            modelAndView = new ModelAndView("redirect:/account/withdraw1");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return modelAndView;
        }
        String message = depositandwithdraw(Config.DEBIT, transaction, name, role, authentication);
        if(message.contains("Success")){
            logService.saveLog(authentication.getName(), "Withdraw money "+transaction.getAccount_no()+" $"+transaction.getTransaction_amount());
        }
        redirectAttributes.addFlashAttribute("message", message);
        return modelAndView;
    }

    private String depositandwithdraw(int type, Transaction transaction, String name, int role, Authentication authentication){
//        Long id =  userService.findUserByEmail(name);
        if(role == Config.USER){
            User user = userService.getUserByUserId(userService.findUserByEmail(authentication.getName()));
            boolean flag = false;
            for(Account account: user.getAccounts()){
                if(transaction.getAccount_no().equals(account.getAccountNo())){
                    flag = true;
                }
            }
            if(!flag){
                return "No authorization for this Account!";
            }
        }
        transaction.setTransaction_timestamp(new Timestamp(System.currentTimeMillis()));
        Account account;
        try{
            account = accountService.getAccountByAccountNo(transaction.getAccount_no());
        }
        catch(Exception e){
            return "Account number doesn't exists!";
        }
        if(transaction.getTransaction_amount() < 0){
            return "Amount cannot be negative!";
        }
        float balance = account.getBalance();
        if (type == Config.DEBIT){
            if (account.getBalance() - transaction.getTransaction_amount() < 0){
                return "Insufficient balance!";
            }
            account.setBalance(account.getBalance() - transaction.getTransaction_amount());
            transaction.setTransaction_type(Config.DEBIT);
            transaction.setBalance(balance - transaction.getTransaction_amount());
            transaction.setDescription("Withdraw "+transaction.getTransaction_amount()+ " || Comments: "+transaction.getDescription());
        }else{
            account.setBalance(account.getBalance() + transaction.getTransaction_amount());
            transaction.setTransaction_type(Config.CREDIT);
            transaction.setBalance(balance + transaction.getTransaction_amount());
            transaction.setDescription("Deposit "+transaction.getTransaction_amount()+ " || Comments: "+transaction.getDescription());
        }

        if(transaction.getTransaction_amount() > Config.LIMIT || role == Config.USER){
            TransactionRequest transactionRequest = new TransactionRequest();
            transactionRequest.setCreated_by(name);
            transactionRequest.setFrom_account(transaction.getAccount_no());
            transactionRequest.setStatus_id(Config.PENDING);
            transactionRequest.setTransaction_amount(transaction.getTransaction_amount());
            transactionRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));
            if(transaction.getTransaction_amount() > Config.LIMIT ){
                transactionRequest.setCritical(Config.CRITICAL_YES);
            }
            else{
                transactionRequest.setCritical(Config.CRITICAL_NO);
            }
            if (type == Config.DEBIT){
                transactionRequest.setType(Config.WITHDRAW);
                transactionRequest.setDescription(transaction.getDescription());
            }else{
                transactionRequest.setType(Config.DEPOSIT);
                transactionRequest.setDescription(transaction.getDescription());
            }
            account.setBalance(balance);
            Long request_id = transactionRequestService.saveOrUpdate(transactionRequest).getRequest_id();
            transaction.setRequest_id(request_id);
            transaction.setStatus(Config.PENDING);
            transaction.setBalance(balance);
        }else{
            transaction.setStatus(Config.APPROVED);
            accountService.saveOrUpdate(account);
        }
        transactionService.saveOrUpdate(transaction);
        if(role == Config.USER || transaction.getTransaction_amount() > Config.LIMIT ){
            return "Success! Pending approval from Bank authorities!";
        }
        return "Success";
    }
}
