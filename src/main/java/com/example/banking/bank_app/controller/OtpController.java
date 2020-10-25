package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.Auth_user;
import com.example.banking.bank_app.service.EmailService;
import com.example.banking.bank_app.service.OtpService;
import com.example.banking.bank_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping(value="/otp")
public class OtpController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @RequestMapping(value="/generateOtp/{email}", method= RequestMethod.GET)
    public Boolean generateOtp(@PathVariable("email") String email, Authentication authentication) {
        int otp = otpService.generateOTP();
        if(authentication != null && email == null){
            email = authentication.getName();
        }
        try {
            Auth_user authUser = userService.findByEmail(email);
            authUser.setOtp(otp);
            authUser.setExpiry(new Timestamp(System.currentTimeMillis()));
            userService.saveOrUpdate(authUser);

            String message = "Your One Time Password is " + otp;
            emailService.sendOtpMessage(email, "Secure Bank - One Time Password", message);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @RequestMapping(value="/generateOtp", method= RequestMethod.GET)
    public Boolean generateOtp(Authentication authentication) {
        int otp = otpService.generateOTP();
        String email = "";
        if(authentication != null){
            email = authentication.getName();
        }
        try {
            Auth_user authUser = userService.findByEmail(email);
            authUser.setOtp(otp);
            authUser.setExpiry(new Timestamp(System.currentTimeMillis()));
            userService.saveOrUpdate(authUser);

            String message = "Your One Time Password is " + otp;
            emailService.sendOtpMessage(email, "Secure Bank - One Time Password", message);
        }catch (Exception e){
            return false;
        }
        return true;
    }

}
