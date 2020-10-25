package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Long> {

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u.userId FROM User u WHERE u.emailId =:email")
    Long findUserByEmail(@Param("email") String email);

    @Query("SELECT u.userId FROM User u WHERE u.contact =:phone")
    Long findUserByPhone(@Param("phone") String phone);

}


