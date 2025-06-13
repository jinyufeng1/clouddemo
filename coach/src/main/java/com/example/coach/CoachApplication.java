package com.example.coach;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
@MapperScan("com.example.coach.mapper")
public class CoachApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoachApplication.class, args);
    }

}
