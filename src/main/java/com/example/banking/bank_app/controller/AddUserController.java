package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.*;
import com.example.banking.bank_app.service.AccountService;
import com.example.banking.bank_app.service.CardService;
import com.example.banking.bank_app.service.LogService;
import com.example.banking.bank_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Date;

@Controller

public class AddUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private LogService logService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/addUser", method = RequestMethod.GET)
    public ModelAndView AddUserForm(@ModelAttribute("message") String message) {
        ModelAndView modelAndView = new ModelAndView();
        AddUser user = new AddUser();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("addUser"); // resources/template/register.html
        modelAndView.addObject("message",message);
        return modelAndView;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String formSubmit(@Valid AddUser adduser, BindingResult bindingResult, Model model,Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return "redirect:/addUser";
        }
        if(!checkvalidPassword(adduser.getPassword())){
            redirectAttributes.addFlashAttribute("message","Password doesnt match requirements!");
            return "redirect:/addUser";
        }
        Auth_user auth_user = new Auth_user();
        auth_user.setEmail(adduser.getEmailId());
        try {
            User u = userService.getUserByUserId(userService.findUserByPhone(adduser.getContact()));
            if (u != null){
                redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
                return "redirect:/addUser";
            }
        }catch (Exception e){
            System.out.println("Exception");
        }
        if(userService.userAlreadyExist(auth_user)){
            redirectAttributes.addFlashAttribute("message", "Email already exists!");
            return "redirect:/addUser";
        }
        Date today = new Date();
        if(adduser.getDob().after(today)){
            redirectAttributes.addFlashAttribute("message","Date of Birth Cannot be greater than today!");
            return "redirect:/addUser";
        }
        if(!adduser.getGender().equals("M")&&!adduser.getGender().equals("F")){
            redirectAttributes.addFlashAttribute("message","Invalid Gender!");
            return "redirect:/addUser";
        }
        if(adduser.getContact() == null || !adduser.getContact().matches("-?\\d+(\\.\\d+)?") || adduser.getContact().length() != 10){
            redirectAttributes.addFlashAttribute("message","Contact Number not valid!");
            return "redirect:/addUser";
        }
        if(adduser.getBalance() < 0){
            redirectAttributes.addFlashAttribute("message","Balance cannot be negative!");
            return "redirect:/addUser";
        }
        if(adduser.getInterest() <= 0){
            redirectAttributes.addFlashAttribute("message","Interest Should be greater than 0!");
            return "redirect:/addUser";
        }
        if(adduser.getAccountType() < Config.CHECKINGS || adduser.getAccountType() > Config.CREDITCARD){
            redirectAttributes.addFlashAttribute("message","Invalid Account type!");
            return "redirect:/addUser";
        }


        User new_user = new User();
        new_user.setName(adduser.getName());
        new_user.setGender(adduser.getGender());
        new_user.setDob(adduser.getDob());
        new_user.setContact(adduser.getContact());
        new_user.setEmailId(adduser.getEmailId());
        new_user.setAddress(adduser.getAddress());
        new_user.setUserType(adduser.getUserType());
        new_user.setCreated(new Timestamp(System.currentTimeMillis()));
        User user=userService.saveOrUpdate(new_user);
        SecureRandom random = new SecureRandom();
        int routing = random.nextInt(100000);
        Account account = new Account();
        account.setUserId(user.getUserId());
        account.setBalance(adduser.getBalance());
        account.setRoutingNo(routing);
        account.setAccountType(adduser.getAccountType());
        account.setInterest(adduser.getInterest());
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
        auth_user.setPassword(adduser.getPassword());
        auth_user.setName(adduser.getName());
        auth_user.setLastName("");
        Auth_user newAuthUser = userService.saveUser(auth_user);

        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setAuth_user_id(new Long(newAuthUser.getId()));
        authUserRole.setAuth_role_id(new Long(4));
        userService.save(authUserRole);
        System.out.println("The user & account added successfully");
        redirectAttributes.addFlashAttribute("message","Successfully created");
        logService.saveLog(authentication.getName(),"New User & Account created for "+adduser.getEmailId());
        return "redirect:/addUser";
    }

    public boolean checkvalidPassword(String password) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        return password.matches(pattern);
    }

}
