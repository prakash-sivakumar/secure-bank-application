package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Auth_user;
import com.example.banking.bank_app.respository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    @Autowired
    private AuthUserRepository authUserRepository;


    public int generateOTP(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return otp;
    }

    public Boolean validateOtp(String otp, String email) {
        Auth_user auth_user = authUserRepository.findUserByEmail(email);
        Timestamp time = auth_user.getExpiry();
        time.setTime(time.getTime() + TimeUnit.MINUTES.toMillis(10));
        if (time.before(new Timestamp(System.currentTimeMillis()))) {
            return false;
        }
        try {
            if (auth_user.getOtp() == Integer.parseInt(otp)) {
                return true;
            }
        }
        catch (Exception e){
            return false;
        }
        return false;
    }

}
