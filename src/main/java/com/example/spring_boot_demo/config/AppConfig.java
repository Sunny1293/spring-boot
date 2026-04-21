package com.example.spring_boot_demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
      return new ModelMapper();
  }

}
