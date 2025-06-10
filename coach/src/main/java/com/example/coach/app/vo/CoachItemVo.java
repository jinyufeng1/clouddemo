package com.example.coach.app.vo;

import lombok.Data;
import com.example.common.domain.ImageVo;

//列表元素
@Data
public class CoachItemVo {
//    数据id
    private Long id;

//    封面
    private ImageVo pic;

//    名称
    private String name;

//    专长
    private String speciality;

//    分类
    private String category;
}
