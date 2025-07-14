package com.example.consumerapp.custom.aop;

// 实现类
public class UserServiceImpl implements UserService {
    @Loggable
    @Override
    public void saveUser() {
        System.out.println("保存用户到数据库...");
    }
}
