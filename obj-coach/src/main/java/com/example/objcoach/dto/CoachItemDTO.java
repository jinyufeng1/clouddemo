package com.example.objcoach.dto;

import lombok.Data;

//列表元素
@Data
public class CoachItemDTO {
    //    数据id
    private Long id;

    //    封面
    private String pics;

    //    名称
    private String name;

    //    专长
    private String speciality;

    //    分类
    private String category;
}
