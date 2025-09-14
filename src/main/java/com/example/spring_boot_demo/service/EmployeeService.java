package com.example.spring_boot_demo.service;

import com.example.spring_boot_demo.dto.EmployeeRequestDto;
import com.example.spring_boot_demo.dto.EmployeeResponseDto;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    List<EmployeeResponseDto> getAllEmployees();

    EmployeeResponseDto getEmployeeById(Long id);

    EmployeeResponseDto createNewEmployee(EmployeeRequestDto employeeRequestDto);

    void deleteEmployeeById(Long id);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto employeeRequestDto);

    EmployeeResponseDto updatePartialEmployee(Long id, Map<String, Object> updates);
}
