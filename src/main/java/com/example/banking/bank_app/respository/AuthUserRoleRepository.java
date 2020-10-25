package com.example.banking.bank_app.respository;

import com.example.banking.bank_app.model.AuthUserRole;
import com.example.banking.bank_app.model.AuthUserRolePK;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRoleRepository extends CrudRepository<AuthUserRole, AuthUserRolePK> {


}
