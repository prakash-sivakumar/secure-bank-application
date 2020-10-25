package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees();

    Page<Employee> getPaginated(Pageable pageable, int tier);

    void deleteEmployee(Long id);

    void saveOrUpdate(Employee employee);

    Employee getEmployeeById(Long id);

    Long findUserByEmail(String email);

    Long findUserByPhone(String contact);
}

