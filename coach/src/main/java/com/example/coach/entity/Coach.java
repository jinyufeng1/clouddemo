package com.example.coach.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Coach {
    private Long id;
    private String name;
    private String pics;
    private String speciality;
    private String intro;
    private Long categoryId;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
