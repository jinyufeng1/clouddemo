package com.example.consumerapp.aspect;

import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    // 定义一个切入点，匹配所有需要权限验证的方法 描述哪个包下的哪个类的哪个方法
    @Pointcut("execution(* com.example.consumerapp.controller.TestAspectController.*(..))")
    public void securedMethods() {}

    // 在方法执行前进行权限验证
//    @Before("securedMethods()")
    // 同一个切点可以写在@Pointcut里和多个方法绑定 不同的切点可以直接写在@Before @After 等注解里和方法绑定
    @Before("execution(* com.example.consumerapp.controller.TestAspectController.*(..))")
    public void beforeAdvice(JoinPoint joinPoint) {
        // 假设从上下文中获取当前用户
//        User currentUser = getCurrentUser();
//        if (currentUser == null || !currentUser.hasPermission("ACCESS")) {
//            throw new SecurityException("Access denied");
//        }

//        throw new SecurityException("Access denied");
        Signature signature = joinPoint.getSignature();
        System.out.println("Access granted to " + joinPoint.getSignature().getName());
    }


//    private User getCurrentUser() {
//        // 获取当前用户逻辑
//        return new User("123", "John Doe", true);
//    }
}
