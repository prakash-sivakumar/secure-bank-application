package com.example.banking.bank_app.model;

import java.io.Serializable;

public class AuthUserRolePK implements Serializable {

    private Long auth_user_id;

    private Long auth_role_id;

    public AuthUserRolePK() {
    }

    public AuthUserRolePK(Long auth_user_id, Long auth_role_id) {
        this.auth_user_id = auth_user_id;
        this.auth_role_id = auth_role_id;
    }

    public Long getAuth_user_id() {
        return auth_user_id;
    }

    public void setAuth_user_id(Long auth_user_id) {
        this.auth_user_id = auth_user_id;
    }

    public Long getAuth_role_id() {
        return auth_role_id;
    }

    public void setAuth_role_id(Long auth_role_id) {
        this.auth_role_id = auth_role_id;
    }
}
