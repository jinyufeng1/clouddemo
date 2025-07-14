package com.example.consumerapp.custom.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AopInvocationHandler implements InvocationHandler {

    private final Object target; // 目标对象

    public AopInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 模拟切点，符合切点，代理对象才做增强操作
        // 判断方法上是否有 @Loggable 注解 疑问 jdk代理的方式，在实现类的方法上写了注解，这里感知不到 ？？？
        if (method.isAnnotationPresent(Loggable.class)) {
            System.out.println("[AOP 前置] 方法：" + method.getName());
        }

        // 调用目标方法
        Object result = method.invoke(target, args);

        if (method.isAnnotationPresent(Loggable.class)) {
            System.out.println("[AOP 后置] 方法执行完毕");
        }

        return result;
    }
}