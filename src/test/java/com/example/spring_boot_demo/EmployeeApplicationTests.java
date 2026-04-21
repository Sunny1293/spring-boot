package com.example.spring_boot_demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;


@Slf4j
class EmployeeApplicationTests {

	@BeforeEach
	void setUp() {
		log.info("Starting the method, setting up config");
	}

	@AfterEach
	void tearDown() {
		log.info("Tearing down the method");
	}

	@BeforeAll
	static void setUpOnce() {
		log.info("Setup Once...");
	}

	@AfterAll
	static void tearDownOnce() {
		log.info("Tearing down all...");
	}

}
