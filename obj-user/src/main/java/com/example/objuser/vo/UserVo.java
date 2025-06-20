package com.example.objuser.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVo {
    private String name;
    private String phone;
    private String avatar;
    private String sign;
}
