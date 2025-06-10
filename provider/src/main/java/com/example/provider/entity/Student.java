package com.example.provider.entity;

import lombok.Data;

/**
 * <p>
 * 学生表
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-06-01
 */
@Data
public class Student {
    private Long id;
    private String name;
    private Integer gender;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
