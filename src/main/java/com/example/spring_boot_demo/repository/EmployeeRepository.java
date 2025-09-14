package com.example.spring_boot_demo.repository;

import com.example.spring_boot_demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
