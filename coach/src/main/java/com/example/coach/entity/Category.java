package com.example.coach.entity;

import lombok.Data;

/**
 * <p>
 * 分类信息表
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-02
 */
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
