package com.example.banking.bank_app.model;

import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name="transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaction_id;

    @Column(name="transaction_amount")
    private float transaction_amount;

    @Column(name="transaction_timestamp")
    private Timestamp transaction_timestamp;

    @Column(name="description")
    @SafeHtml
    private String description;

    @Column(name="status")
    private Integer status;

    @NotNull(message="Account number cannot be empty")
    @Column(name="account_no")
    private Long account_no;

    @Column(name="balance")
    private float balance;

    @Column(name="transaction_type")
    private int transaction_type;

    @Column(name="request_id")
    private Long request_id;

    @ManyToOne
    @JoinColumn(name="request_id",referencedColumnName="request_id", insertable=false, updatable=false)
    private TransactionRequest request;


    public Long getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(Long transaction_id) {
        this.transaction_id = transaction_id;
    }

    public float getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(float transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public Date getTransaction_timestamp() {
        return transaction_timestamp;
    }

    public void setTransaction_timestamp(Timestamp transaction_timestamp) {
        this.transaction_timestamp = transaction_timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAccount_no() {
        return account_no;
    }

    public void setAccount_no(Long account_no) {
        this.account_no = account_no;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public TransactionRequest getRequest() {
        return request;
    }

    public void setRequest(TransactionRequest request) {
        this.request = request;
    }

    public int getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(int transaction_type) {
        this.transaction_type = transaction_type;
    }

    public Long getRequest_id() {
        return request_id;
    }

    public void setRequest_id(Long request_id) {
        this.request_id = request_id;
    }
}
