package com.example.feign.coach.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
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
