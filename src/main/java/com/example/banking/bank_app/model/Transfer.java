package com.example.banking.bank_app.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Transfer {

    @NotNull(message="Account number cannot be empty")
    private Long from_account_no;

    @NotNull(message="Account number cannot be empty")
    private Long to_account_no;

    @Min(1)
    @NotNull(message="Amount cannot be empty")
    private float transaction_amount;

    private String description;

    private String otp;

    public Long getFrom_account_no() {
        return from_account_no;
    }

    public void setFrom_account_no(Long from_account_no) {
        this.from_account_no = from_account_no;
    }

    public Long getTo_account_no() {
        return to_account_no;
    }

    public void setTo_account_no(Long to_account_no) {
        this.to_account_no = to_account_no;
    }

    public float getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(float transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
