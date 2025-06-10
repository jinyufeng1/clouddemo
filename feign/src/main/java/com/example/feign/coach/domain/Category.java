package com.example.feign.coach.domain;

import lombok.Data;

@Data
public class Category {
    private Long id;
    private String name;
    private String pic;
    private Long parentId;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
