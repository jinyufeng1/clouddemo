package com.example.consumerapp.verification.login.entity;

import com.example.objuser.entity.User;
import lombok.Data;

@Data
public class CurrentUser {
    private User userInfo;
    private Integer code = 1001;
}