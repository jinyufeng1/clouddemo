package com.example.consumerapp.service;

import org.springframework.stereotype.Service;

@Service
public class TestAspectService {
    public void testLoggingAspect() {
        // 业务逻辑
        System.out.println("public void testAspect()");
    }

    public void testTransactionAspect() {
        // 业务逻辑
        System.out.println("public void testTransactionAspect()");
    }
}
