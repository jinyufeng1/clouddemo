package com.example.consumerconsole.verification.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，并指定拦截路径
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns("/user/login"); // 排除登录
    }
}
