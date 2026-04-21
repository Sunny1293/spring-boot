package com.example.spring_boot_demo.controller;

import com.example.spring_boot_demo.dto.EmployeeRequestDto;
import com.example.spring_boot_demo.dto.EmployeeResponseDto;
import com.example.spring_boot_demo.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.List;

@AutoConfigureWebTestClient(timeout = "100000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import(TestContainerConfiguration.class)
public class AbstractIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    Employee testEmployee = Employee.builder()
            .id(1L).email("sunny@gmail.com")
            .name("Sunny")
            .salary(200L)
            .build();

    EmployeeRequestDto testEmployeeRequestDto = EmployeeRequestDto.builder()
            .id(1L)
            .email("sunny@gmail.com")
            .name("Sunny")
            .salary(200L)
            .build();

    EmployeeResponseDto testEmployeeResponseDto = EmployeeResponseDto.builder()
            .id(1L)
            .email("sunny@gmail.com")
            .name("Sunny")
            .salary(200L)
            .build();

    List<Employee> testEmployeeList = List.of(testEmployee);

}

