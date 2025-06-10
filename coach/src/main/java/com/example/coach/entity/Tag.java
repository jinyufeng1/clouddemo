package com.example.coach.entity;

import lombok.Data;

/**
 * <p>
 * 标签信息表
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-28
 */
@Data
public class Tag {
    private Long id;
    private String name;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
