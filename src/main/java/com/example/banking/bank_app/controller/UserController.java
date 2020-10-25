package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.Account;
import com.example.banking.bank_app.model.AccountRequest;
import com.example.banking.bank_app.model.Config;
import com.example.banking.bank_app.model.User;
import com.example.banking.bank_app.service.AccountRequestService;
import com.example.banking.bank_app.service.AccountService;
import com.example.banking.bank_app.service.LogService;
import com.example.banking.bank_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value="/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private LogService logService;

    @Autowired
    private AccountRequestService accountRequestService;

    @RequestMapping(value="/list/{page}", method= RequestMethod.GET)
    public ModelAndView list(@PathVariable("page") int page) {
        ModelAndView modelAndView = new ModelAndView("user_list");
        PageRequest pageable = PageRequest.of(page - 1, 15);
        Page<User> userPage = userService.getPaginated(pageable);
        int totalPages = userPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNos = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNos", pageNos);
        }
        modelAndView.addObject("activeUserList", true);
        modelAndView.addObject("userList", userPage.getContent());
        return modelAndView;
    }

    @RequestMapping(value = "/accounts/{type}/{data}", method= RequestMethod.GET)
    public List<Account> getAccounts(@PathVariable String type, @PathVariable String data){
        long userId;
        List<Account> accounts = new ArrayList<>() ;
        if(data!=null){
            switch (type) {
                case Config.EMAIL:
                    userId = userService.findUserByEmail(data);
                    accounts = userService.getUserByUserId(userId).getAccounts();
                    break;
                case Config.PHONE:
                    userId = userService.findUserByPhone(data);
                    accounts = userService.getUserByUserId(userId).getAccounts();
                    break;
            }
        }
        return accounts;
    }

    @RequestMapping(value="/edit", method= RequestMethod.POST)
    public ModelAndView editAccount(@Valid User user, BindingResult bindingResult,Authentication authentication,  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return new ModelAndView("redirect:/user");
        }
        Long id =  userService.findUserByEmail(authentication.getName());
        User old_user = userService.getUserByUserId(id);
//        old_user.setAddress(user.getAddress());
//        old_user.setContact(user.getContact());
//        old_user.setEmailId(user.getEmailId());
//        old_user.setDob(user.getDob());
        if(!user.getEmailId().equals(old_user.getEmailId()) || !user.getName().equals(old_user.getName())){
            redirectAttributes.addFlashAttribute("message","Cannot change name or email!");
            return new ModelAndView("redirect:/user");
        }
        if(user.getContact() == null || !user.getContact().matches("-?\\d+(\\.\\d+)?") || user.getContact().length() != 10){
            redirectAttributes.addFlashAttribute("message","Contact Number not valid!");
            return new ModelAndView("redirect:/user");
        }
        Date today = new Date();
        if(user.getDob().after(today)){
            redirectAttributes.addFlashAttribute("message","Date of Birth Cannot be greater than today!");
            return new ModelAndView("redirect:/user");
        }
        try{
            User user1 = userService.getUserByUserId(userService.findUserByPhone(user.getContact()));
            if( user1 != null && !user1.getUserId().equals(old_user.getUserId())){
                redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
                return new ModelAndView("redirect:/user");
            }
        }
        catch (Exception e){
            System.out.println("Exception");
        }
//        userService.saveOrUpdate(old_user);
        AccountRequest accountRequest = new AccountRequest();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_id",old_user.getUserId());
        attributes.put("name", old_user.getName());
        attributes.put("gender", old_user.getGender());
        attributes.put("dob", user.getDob());
        attributes.put("contact", user.getContact());
        attributes.put("email_id",old_user.getEmailId());
        attributes.put("address", user.getAddress());
        accountRequest.setDescription("Profile Edit for "+ user.getName() +" : "+user.getEmailId() );
        accountRequest.setUser(attributes);
        accountRequest.setCreated_by(old_user.getName());
        accountRequest.setStatus_id(Config.PENDING);
        accountRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));
        accountRequest.setType(Config.USER_TYPE);
        accountRequest.setRole(Config.TIER1);
        try {
            accountRequest.serializeuser();
        }
        catch(Exception e){
            System.out.println("Exception");
        }
        accountRequestService.saveOrUpdate(accountRequest);

        redirectAttributes.addFlashAttribute("message","Successfully saved data! Pending approval with authorities!");
        logService.saveLog(authentication.getName(),"Changed User profile for "+old_user.getEmailId());
        return new ModelAndView("redirect:/user");
    }
}
