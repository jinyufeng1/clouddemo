package com.example.user.entity;

import lombok.Data;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-19
 */
@Data
public class User {
    private Long id;
    private String name;
    private String password;
    private String phone;
    private String avatar;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
