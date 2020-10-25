package com.example.banking.bank_app.model;

import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="help_page")

public class Help {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long help_id;

    @Column(name="auth_user_id")
    private Long auth_user_id;

    @Column(name="mobile")
//    @NotNull
//    @Pattern(regexp = "^[2-9]\\d{2}-\\d{3}-\\d{4}$", message="Phone Number Format is XXX-YYY-ZZZZ")
    private String mobile;


    @Column(name="email")
//    @NotNull
//    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message="Email address is invalid")
    private String email;

    @Column(name="shortdescription")
    @NotNull()
    @Size(min=2,max=200, message="Description should be between 2 to 200 characters")
    @SafeHtml
    private String shortdescription;

    @Column(name="title")
    @NotNull()
    @Size(min=2,max=32,message = "Title should be between 2 to 32 characters")
    @SafeHtml
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getHelp_id() {
        return help_id;
    }

    public void setHelp_id(Long help_id) {
        this.help_id = help_id;
    }

    public Long getAuth_user_id() {
        return auth_user_id;
    }

    public void setAuth_user_id(Long auth_user_id) {
        this.auth_user_id = auth_user_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShortdescription() {
        return shortdescription;
    }

    public void setShortdescription(String shortdescription) {
        this.shortdescription = shortdescription;
    }
}
