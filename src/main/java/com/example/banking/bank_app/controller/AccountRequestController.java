package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.*;
import com.example.banking.bank_app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value="/account-request")
public class AccountRequestController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private LogService logService;

    @Autowired
    private AccountRequestService accountRequestService;

    @RequestMapping(value="/list/{page}", method= RequestMethod.GET)
    public ModelAndView list(@PathVariable("page") int page, Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        int role;
        ModelAndView modelAndView;
        if(roles.contains("ADMIN")){
            role = Config.ADMIN;
            modelAndView = new ModelAndView("account_request_admin");
        }else if(roles.contains("TIER1")){
            role = Config.TIER1;
            modelAndView = new ModelAndView("account_request");
        }
        else{
            role = Config.TIER2;
            modelAndView = new ModelAndView("account_request");
        }

        PageRequest pageable = PageRequest.of(page - 1, 10);
        Page<AccountRequest> requestPage = accountRequestService.getPaginated(pageable, role);
        int totalPages = requestPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNums = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNums", pageNums);
        }
        modelAndView.addObject("role", role);
        modelAndView.addObject("activeRequestList", true);
        modelAndView.addObject("requestList", requestPage.getContent());
        return modelAndView;
    }

    @RequestMapping(value="/approve/{id}", method= RequestMethod.POST)
    public ModelAndView approve(@PathVariable("id") int id, Authentication authentication)  throws IOException {
        Long userId =  employeeService.findUserByEmail(authentication.getName());
        String name = employeeService.getEmployeeById(userId).getEmployee_name();
        AccountRequest accountRequest = accountRequestService.getAccountByAccountNo(new Long(id));
        if(accountRequest.getType() == Config.ACCOUNT_TYPE){
            accountRequest.deserializeaccount();
            Map<String, Object> attributes = accountRequest.getAccountJson();
            Account account = new Account();
            account.setAccountNo((Long)attributes.get("account_no"));
            account.setUserId(new Long((Integer)attributes.get("user_id")));
            int balance = (int)attributes.get("balance");
            account.setBalance((float)balance);
            account.setRoutingNo((Integer)attributes.get("routing_no"));
            account.setAccountType((Integer)attributes.get("account_type"));
            double interest = (double)attributes.get("interest");
            account.setInterest((float)interest);
            account.setCreated(new Timestamp(System.currentTimeMillis()));
            account.setUpdated(new Timestamp(System.currentTimeMillis()));
            Account new_account = accountService.saveOrUpdate(account);
            if(account.getAccountType() == Config.CREDITCARD){
                Card card = new Card();
                card.setAccountNo(new_account.getAccountNo());
                card.setBalance(0f);
                card.setCreditLimit(Config.DEFAULT_CREDIT_LIMIT);
                card.setType(Config.CREDIT);
                card.setCreated(new Timestamp(System.currentTimeMillis()));
                card.setUpdated(new Timestamp(System.currentTimeMillis()));
                cardService.saveOrUpdate(card);
            }
        }else if (accountRequest.getType() == Config.USER_TYPE){
            accountRequest.deserializeuser();
            Map<String, Object> attributes = accountRequest.getUserJson();
            User user = new User();
            Integer d =(Integer) attributes.get("user_id");
            User old_user = userService.getUserByUserId(new Long(d));
            user.setUserId(old_user.getUserId());
            user.setName((String)attributes.get("name"));
            user.setGender((String) attributes.get("gender"));
            Long dob = (Long) attributes.get("dob");
            user.setDob(new Timestamp(dob));
            user.setContact((String) attributes.get("contact"));
            user.setEmailId((String) attributes.get("email_id"));
            user.setAddress((String) attributes.get("address"));
            user.setUserType(old_user.getUserType());
            user.setCreated(old_user.getCreated());
            userService.saveOrUpdate(user);

        }else if (accountRequest.getType() == Config.EMPLOYEE_TYPE){
            accountRequest.deserializeemployee();
            Map<String, Object> attributes = accountRequest.getEmployeeJson();
            Employee employee = new Employee();
            Integer employeeID = (Integer)attributes.get("employee_id");
            Employee old_employee = employeeService.getEmployeeById(new Long(employeeID));
            employee.setEmployee_id(new Long(employeeID));
            employee.setEmployee_name((String) attributes.get("employee_name"));
            employee.setGender((String) attributes.get("gender"));
            employee.setAge((Integer) attributes.get("age"));
            employee.setAddress((String) attributes.get("address")) ;
            employee.setContact_no((String) attributes.get("contact_no"));
            employee.setDesignation_id((Integer) attributes.get("designation_id"));
            employee.setTier_level((Integer) attributes.get("tier_level"));
            employee.setEmail_id((String) attributes.get("email_id"));
            employee.setCreated(old_employee.getCreated());
            employee.setUpdated(new Timestamp(System.currentTimeMillis()));
            employeeService.saveOrUpdate(employee);
        }
        accountRequest.setApproved_at(new Timestamp(System.currentTimeMillis()));
        accountRequest.setApproved_by(name); //Remeber to change this
        accountRequest.setStatus_id(Config.APPROVED);
        accountRequestService.saveOrUpdate(accountRequest);
        logService.saveLog(authentication.getName(), "Approved Request for id:"+id);
        return new ModelAndView("redirect:/account-request/list/1");
    }

    @RequestMapping(value="/decline/{id}", method= RequestMethod.POST)
    public ModelAndView decline(@PathVariable("id") int id, Authentication authentication) {
        Long userId =  employeeService.findUserByEmail(authentication.getName());
        String name = employeeService.getEmployeeById(userId).getEmployee_name();
        AccountRequest accountRequest = accountRequestService.getAccountByAccountNo(new Long(id));
        accountRequest.setApproved_at(new Timestamp(System.currentTimeMillis()));
        accountRequest.setApproved_by(name); //Remeber to change this
        accountRequest.setStatus_id(Config.DECLINED);
        accountRequestService.saveOrUpdate(accountRequest);
        logService.saveLog(authentication.getName(), "Declined Request for id:"+id);
        return new ModelAndView("redirect:/account-request/list/1");
    }

}
