package com.example.objcoach.vo.console;

import lombok.Data;

import java.util.List;

@Data
public class CategoryItemTreeVo {
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
    private List<CategoryItemTreeVo> children;
}
