package com.example.banking.bank_app.model;

import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class AddUser {


    @NotNull(message="Name cannot be empty")
    @SafeHtml
    private String name;

    @NotNull(message="Gender cannot be empty")
    @SafeHtml
    private String gender;

    @NotNull(message="Gender cannot be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @NotNull(message="Contact cannot be empty")
    @SafeHtml
    private String contact;

    @NotNull(message="Email id cannot be empty")
    @SafeHtml
    private String emailId;

    @NotNull(message="Address cannot be empty")
    @SafeHtml
    private String address;

    private float balance;

    private Integer routingNo;

    private Integer accountType;

    private float interest;

    private String password;

    private Integer userType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Integer getRoutingNo() {
        return routingNo;
    }

    public void setRoutingNo(Integer routingNo) {
        this.routingNo = routingNo;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public float getInterest() {
        return interest;
    }

    public void setInterest(float interest) {
        this.interest = interest;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }
}
