package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.Account;
import com.example.banking.bank_app.model.Auth_user;
import com.example.banking.bank_app.model.Employee;
import com.example.banking.bank_app.model.User;
import com.example.banking.bank_app.service.EmployeeService;
import com.example.banking.bank_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String main(Authentication authentication) { // resources/template/login.html
        if (authentication != null && authentication.isAuthenticated()){
            return "redirect:/home";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public ModelAndView login(Authentication authentication) {
        if(authentication != null){
            authentication.setAuthenticated(false);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login"); // resources/template/login.html
        return modelAndView;
    }

    @RequestMapping(value = { "/denied" }, method = RequestMethod.GET)
    public ModelAndView denied() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("denied"); // resources/template/login.html
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        Auth_user user = new Auth_user();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register"); // resources/template/register.html
        return modelAndView;
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(Authentication authentication) {
        String url = "/login?error=true";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        if(roles.contains("ADMIN")) {
            url = "/admin";
        }
        else if(roles.contains("TIER1")) {
            url = "/tier1";
        }
        else if(roles.contains("TIER2")) {
            url = "/tier2";
        }
        else if(roles.contains("USER")) {
            url = "/user";
        }
        else if(roles.contains("MERCHANT")) {
            url = "/merchant";
        }
        return "redirect:"+url;
    }
    @RequestMapping (value = "/register", method = RequestMethod.POST)
    public ModelAndView registerUser(@Valid Auth_user user, BindingResult bindingResult, ModelMap modelMap) {
        ModelAndView modelAndView = new ModelAndView();

        //check for the validations
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("successMessage", "Please Correct the Errors!");
            modelMap.addAttribute("bindingResult", bindingResult);
        }
        else if (userService.userAlreadyExist(user)){
            modelAndView.addObject("successMessage", "user already exists!");
        }
        // if no errors, then save user
        else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User Registered Successfully");
        }
        modelAndView.addObject("user", new Auth_user());
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ModelAndView admin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin"); // resources/template/admin.html
        return modelAndView;
    }

    @RequestMapping(value = "/tier1", method = RequestMethod.GET)
    public ModelAndView tier1(Authentication authentication) {
        Long id =  employeeService.findUserByEmail(authentication.getName());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("tier1"); // resources/template/tier1.html
        Employee employee = employeeService.getEmployeeById(id);
        modelAndView.addObject("employee",employee);
        return modelAndView;
    }

    @RequestMapping(value = "/tier2", method = RequestMethod.GET)
    public ModelAndView tier2(Authentication authentication, @ModelAttribute("message") String message) {
        Long id =  employeeService.findUserByEmail(authentication.getName());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("tier2"); // resources/template/tier2.html
        Employee employee = employeeService.getEmployeeById(id);
        modelAndView.addObject("employee",employee);
        modelAndView.addObject("message", message);
        return modelAndView;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ModelAndView user(Authentication authentication, @ModelAttribute("message") String message) {
        Long id =  userService.findUserByEmail(authentication.getName());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user"); // resources/template/home.html
        User user = userService.getUserByUserId(id);
        modelAndView.addObject("user",user);
        Account account = new Account();
        modelAndView.addObject("account", account);
        modelAndView.addObject("message", message);
        return modelAndView;
    }

    @RequestMapping(value = "/merchant", method = RequestMethod.GET)
    public ModelAndView merchant() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("merchant"); // resources/template/merchant.html
        return modelAndView;
    }

}
