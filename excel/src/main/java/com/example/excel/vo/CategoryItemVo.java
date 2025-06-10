package com.example.excel.vo;

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

    // 创建时间
    private String createTime;

    // 修改时间
    private String updateTime;

    // 子节点
    private List<CategoryItemVo> children;
}
