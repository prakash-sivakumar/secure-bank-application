package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.AuthUserRole;
import com.example.banking.bank_app.model.AuthUserRolePK;
import com.example.banking.bank_app.model.Auth_user;
import com.example.banking.bank_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<User> getPaginated(Pageable pageable);

    Long findUserByEmail(String email);

    Long findUserByPhone(String phone);

    Auth_user saveUser (Auth_user user);

    boolean userAlreadyExist (Auth_user user);

    List<Auth_user> getAllUsers();

    User getUserByUserId(Long userId);

    void saveOrUpdate(Auth_user user);

//    void saveNewUser(User user);

    void deleteUser(Integer userId);

    User saveOrUpdate(User user);

    void save(AuthUserRole authUserRole);

    void deleteAuthUserRole(AuthUserRole authUserRole);

    void deleteAuthUser(Integer authUserId);

    Auth_user findByEmail(String email);

    AuthUserRole findById(AuthUserRolePK authUserRolePK);
}
