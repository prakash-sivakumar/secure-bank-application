package com.example.banking.bank_app;

import com.example.banking.bank_app.model.Auth_user;
import com.example.banking.bank_app.respository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private AuthUserRepository authUserRepository;

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {
        String verificationCode
                = ((CustomWebAuthenticationDetails) auth.getDetails())
                .getVerificationCode();
        Auth_user auth_user = authUserRepository.findUserByEmail(auth.getName());
        if(auth_user == null){
            throw new BadCredentialsException("Wrong credentials");
        }
        Timestamp time = auth_user.getExpiry();
        time.setTime(time.getTime() + TimeUnit.MINUTES.toMillis(10));
        if (time.before(new Timestamp(System.currentTimeMillis()))) {
            throw new BadCredentialsException("OTP expired.");
        }
        try {
            if (auth_user.getOtp() != Integer.parseInt(verificationCode)) {
                throw new BadCredentialsException("Invalid OTP");
            }
        }
        catch (Exception e){
            throw new BadCredentialsException("Invalid OTP");
        }
        Authentication result = super.authenticate(auth);
        return new UsernamePasswordAuthenticationToken(
                auth_user.getEmail(), result.getCredentials(), result.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}