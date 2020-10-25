package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.Auth_user;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRepository extends CrudRepository<Auth_user, Integer> {


    @Query("SELECT u FROM Auth_user u WHERE u.email =:email")
    Auth_user findUserByEmail(@Param("email") String email);
}