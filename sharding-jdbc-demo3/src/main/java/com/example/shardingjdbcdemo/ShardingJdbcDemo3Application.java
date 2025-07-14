package com.example.shardingjdbcdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
@MapperScan("com.example.shardingjdbcdemo.mapper")
public class ShardingJdbcDemo3Application {

    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcDemo3Application.class, args);
    }

}
