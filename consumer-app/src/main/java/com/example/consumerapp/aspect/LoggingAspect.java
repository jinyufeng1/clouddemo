package com.example.consumerapp.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // 定义一个切入点【返回类型 包名.类名.方法名(参数)】
    // 表示匹配所有方法
    @Pointcut("execution(* com.example.consumerapp.service.TestAspectService.testLoggingAspect())")
    public void allMethods() {}

    // 在方法执行前记录日志
    @Before("allMethods()")
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println("Before: " + joinPoint.getSignature().getName());
    }

    // 在方法正常返回后记录日志
    @AfterReturning(pointcut = "allMethods()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        System.out.println("AfterReturning: " + joinPoint.getSignature().getName() + " returned " + result);
    }

    // 在方法抛出异常后记录日志
    @AfterThrowing(pointcut = "allMethods()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable ex) {
        System.out.println("AfterThrowing: " + joinPoint.getSignature().getName() + " threw " + ex);
    }

    // 在方法执行后记录日志
    // @After通知（Advice）是一定会执行的，只要目标方法被调用，无论目标方法是正常完成还是抛出异常，@After通知都会在方法执行之后执行
    @After("allMethods()")
    public void afterAdvice(JoinPoint joinPoint) {
        System.out.println("After: " + joinPoint.getSignature().getName());
    }
}
