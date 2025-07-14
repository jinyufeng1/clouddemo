package com.example.consumerapp.custom.aop;


import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxyFactory {

    public static Object createProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            // 模拟切点，符合切点，代理对象才做增强操作, 源码使用责任链进行方法增强
            // 判断方法上是否有 @Loggable 注解
            if (method.isAnnotationPresent(Loggable.class)) {
                System.out.println("[AOP 前置] 方法：" + method.getName());
            }

            Object result = proxy.invokeSuper(obj, args);

            if (method.isAnnotationPresent(Loggable.class)) {
                System.out.println("[AOP 后置] 方法执行完毕");
            }
            return result;
        });
        return enhancer.create();
    }
}