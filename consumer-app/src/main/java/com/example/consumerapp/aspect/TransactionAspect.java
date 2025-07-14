package com.example.consumerapp.aspect;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionAspect {

    // 定义一个切入点，匹配所有需要事务管理的方法
    @Pointcut("execution(* com.example.consumerapp.service.TestAspectService.testTransactionAspect())")
    public void transactionalMethods() {}

    // 在方法执行前开启事务
    @Before("transactionalMethods()")
    public void beforeAdvice() {
        System.out.println("Starting transaction");
    }

    // 在方法正常返回后提交事务
    @AfterReturning("transactionalMethods()")
    public void afterReturningAdvice() {
        System.out.println("Committing transaction");
    }

    // 在方法抛出异常后回滚事务
    @AfterThrowing("transactionalMethods()")
    public void afterThrowingAdvice() {
        System.out.println("Rolling back transaction");
    }

    // 在方法执行后清理事务资源
    @After("transactionalMethods()")
    public void afterAdvice() {
        System.out.println("Cleaning up transaction resources");
    }
}