package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.Auth_role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends CrudRepository<Auth_role, Integer> {

    public Auth_role findByRole(String role);





}