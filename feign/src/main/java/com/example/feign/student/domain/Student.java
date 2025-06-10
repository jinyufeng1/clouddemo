package com.example.feign.student.domain;

import lombok.Data;

@Data
public class Student {
    private Long id;
    private String name;
    private Integer gender;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
