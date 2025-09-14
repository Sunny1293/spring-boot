package com.example.spring_boot_demo.service.impl;

import com.example.spring_boot_demo.dto.EmployeeRequestDto;
import com.example.spring_boot_demo.dto.EmployeeResponseDto;
import com.example.spring_boot_demo.entity.Employee;
import com.example.spring_boot_demo.repository.EmployeeRepository;
import com.example.spring_boot_demo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> employeeList = employeeRepository.findAll();
        return employeeList.stream().map(employee -> modelMapper.map(employee,EmployeeResponseDto.class)).toList();
    }

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {
       Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: "+id));
        return modelMapper.map(employee,EmployeeResponseDto.class);
    }

    @Override
    public EmployeeResponseDto createNewEmployee(EmployeeRequestDto employeeRequestDto) {
        Employee newEmployee = modelMapper.map(employeeRequestDto,Employee.class);
        Employee savedEmployee = employeeRepository.save(newEmployee);
        return modelMapper.map(savedEmployee,EmployeeResponseDto.class);
    }

    @Override
    public void deleteEmployeeById(Long id) {
        if(!employeeRepository.existsById(id)){
            throw new IllegalArgumentException("Employee not found with ID: "+id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: "+id));
        modelMapper.map(employeeRequestDto,employee);
        employee = employeeRepository.save(employee);
        return modelMapper.map(employee,EmployeeResponseDto.class);
    }

    @Override
    public EmployeeResponseDto updatePartialEmployee(Long id, Map<String, Object> updates) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: "+id));

        updates.forEach((field, value) -> {
            switch (field) {
                case "name":
                    employee.setName((String) value);
                    break;
                case "email":
                    employee.setEmail((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("Field is not supported");
            }
        });
        Employee savedEmployee = employeeRepository.save(employee);
        return modelMapper.map(savedEmployee, EmployeeResponseDto.class);    }
}
