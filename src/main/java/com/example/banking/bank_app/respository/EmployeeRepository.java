package com.example.banking.bank_app.respository;


import com.example.banking.bank_app.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    @Query("SELECT t FROM Employee t WHERE t.tier_level <> :tier")
    Page<Employee> findAll(Pageable pageable, @Param("tier") int tier);

    @Query("SELECT t.employee_id FROM Employee t WHERE t.email_id =:email")
    Long findUserByEmail(@Param("email") String email);

    @Query("SELECT t.employee_id FROM Employee t WHERE t.contact_no =:contact_no")
    Long findUserByPhone(@Param("contact_no") String contact_no);
}
