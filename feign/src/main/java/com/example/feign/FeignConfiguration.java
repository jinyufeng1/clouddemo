package com.example.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

// @EnableFeignClients如果不放在启动类上就要加 @Component
// @EnableFeignClients注解如果被重复使用，可能会导致Spring容器中出现重复的Bean定义，相当于从新添加一次 feign bean
@Configuration
@EnableFeignClients(basePackages = "com.example.feign")
public class FeignConfiguration {
}
