package com.example.spring_boot_demo.controller;

import com.example.spring_boot_demo.dto.EmployeeRequestDto;
import com.example.spring_boot_demo.entity.Employee;
import com.example.spring_boot_demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EmployeeControllerTestIT extends AbstractIntegrationTest{

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void testGetEmployees() {
        List<Employee> savedEmployeeList = employeeRepository.saveAll(testEmployeeList);
        webTestClient.get()
                .uri("/employees")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetEmployeeById_success() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.get()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedEmployee.getId())
                .jsonPath("$.email").isEqualTo(savedEmployee.getEmail());

//                .value(employeeDto -> {
//                    assertThat(employeeDto.getEmail()).isEqualTo(savedEmployee.getEmail());
//                    assertThat(employeeDto.getId()).isEqualTo(savedEmployee.getId());
//                });
    }

    @Test
    void testGetEmployeeById_Failure() {
        webTestClient.get()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeRequestDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmployee() {
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeRequestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeResponseDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeResponseDto.getName());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        webTestClient.put()
                .uri("/employees/999")
                .bodyValue(testEmployeeRequestDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateTheEmail_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeRequestDto.setName("Random Name");
        testEmployeeRequestDto.setEmail("random@gmail.com");

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(testEmployeeRequestDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeRequestDto.setName("Random Name");
        testEmployeeRequestDto.setSalary(250L);

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(testEmployeeRequestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeRequestDto.class)
                .isEqualTo(testEmployeeRequestDto);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPartialUpdateEmployee(){
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Prepare partial update request
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Himanshu");
        updates.put("email", "himanshu@gmail.com");


        webTestClient.patch()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(updates)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Himanshu")
                .jsonPath("$.email").isEqualTo("himanshu@gmail.com");

    }

    @Test
    void testPartialUpdateEmployee_NotFound() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "NoUser");

        webTestClient.patch()
                .uri("/employees/{id}", 999L)
                .bodyValue(updates)
                .exchange()
                .expectStatus().isNotFound();
    }
}













