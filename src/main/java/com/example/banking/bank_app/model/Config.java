package com.example.banking.bank_app.model;

/**
 * Application constants.
 */

public final class Config {
    //Status fields
    public static final int PENDING = 0;
    public static final int APPROVED = 1;
    public static final int DECLINED = 2;

    //Transaction fields
    public static final int DEBIT = 1;
    public static final int CREDIT = 2;

    //Transaction limit
    public static final float LIMIT = 1000;

    //Transfer type
    public static final String ACCOUNT = "account";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";

    //Request type
    public static final int TRANSFER = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;

    public static final int CHECKINGS = 1;
    public static final int SAVINGS = 2;
    public static final int CREDITCARD = 3;

    public static final int ACCOUNT_TYPE = 1;
    public static final int USER_TYPE = 2;
    public static final int EMPLOYEE_TYPE = 3;

    public static final float DEFAULT_INTEREST = 5;

    public static final float DEFAULT_CREDIT_LIMIT = 1000;

    public static final int CRITICAL_YES = 1;
    public static final int CRITICAL_NO = 0;

    public static final int ADMIN = 1;
    public static final int TIER1 = 2;
    public static final int TIER2 = 3;
    public static final int USER = 4;
    public static final int MERCHANT = 5;

}
