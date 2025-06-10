package com.example.coach.app.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryItemVo {
    // 数据id
    private Long id;

    // 名称
    private String name;

    // 图标
    private String icon;

    // 子节点
    private List<CategoryItemVo> children;
}
