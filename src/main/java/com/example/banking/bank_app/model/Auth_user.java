package com.example.banking.bank_app.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Set;
//import;

@Entity
@Table(name = "auth_user")
public class Auth_user {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_user_id")
    private int id;

    @NotNull(message = "*Please Enter Your Name!")
    @Column(name = "first_name")
    private String name;

    @NotNull (message = "*Please Enter Your Last Name!")
    @Column(name = "last_name")
    private String lastName;

    @NotNull (message = "*Please Enter Your Email Address!")
    @Column(name = "email")
    private String email;

    @NotNull (message = "Please Enter Your Password!")
    @Length(min = 5, message = "*Password must be at least 5 characters")
    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "otp")
    private Integer otp;

    @Column(name = "expiry")
    private Timestamp expiry;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "auth_user_role", joinColumns = @JoinColumn(name = "auth_user_id"), inverseJoinColumns = @JoinColumn(name = "auth_role_id"))
    private Set<Auth_role> roles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Auth_role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Auth_role> roles) {
        this.roles = roles;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Timestamp getExpiry() {
        return expiry;
    }

    public void setExpiry(Timestamp expiry) {
        this.expiry = expiry;
    }
}
