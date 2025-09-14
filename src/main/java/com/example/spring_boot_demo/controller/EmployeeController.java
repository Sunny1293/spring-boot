package com.example.spring_boot_demo.controller;

import com.example.spring_boot_demo.dto.EmployeeRequestDto;
import com.example.spring_boot_demo.dto.EmployeeResponseDto;
import com.example.spring_boot_demo.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
class EmployeeController {

     private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createNewEmployee(@RequestBody @Valid EmployeeRequestDto employeeRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createNewEmployee(employeeRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable Long id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(@PathVariable Long id,
                                                    @RequestBody EmployeeRequestDto employeeRequestDto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employeeRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> updatePartialEmployee(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(employeeService.updatePartialEmployee(id, updates));
    }
}
