package com.example.objcoach.vo.console;

import lombok.Data;


//列表元素
@Data
public class CoachItemVo {
//    数据id
    private Long id;

//    封面
    private String pic;

//    名称
    private String name;

//    专长
    private String speciality;

//    分类
    private String category;
}
