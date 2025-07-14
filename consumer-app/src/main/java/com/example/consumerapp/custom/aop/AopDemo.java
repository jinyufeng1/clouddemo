package com.example.consumerapp.custom.aop;

public class AopDemo {
    public static void main(String[] args) {
        // 创建目标对象
        UserService target = new UserServiceImpl();

        // 创建代理对象
        UserService proxy = JdkProxyFactory.createProxy(target, UserService.class);

        // 调用方法
        System.out.println("代理对象类型：" + proxy.getClass().getName());
        proxy.saveUser();

        System.out.println("----------------------------------------");

        UserServiceImpl proxy1 = (UserServiceImpl) CglibProxyFactory.createProxy(UserServiceImpl.class);

        System.out.println("代理对象类型：" + proxy1.getClass().getName());
        proxy1.saveUser();
    }
}