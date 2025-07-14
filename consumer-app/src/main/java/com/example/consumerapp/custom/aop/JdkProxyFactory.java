package com.example.consumerapp.custom.aop;

import java.lang.reflect.Proxy;

public class JdkProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class<?>[]{interfaceClass},
                new AopInvocationHandler(target)
        );
    }
}