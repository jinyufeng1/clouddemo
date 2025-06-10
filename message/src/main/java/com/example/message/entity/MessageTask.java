package com.example.message.entity;

import lombok.Data;

/**
 * <p>
 * 短信任务表表
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-05-07
 */
@Data
public class MessageTask {
    private Long id;
    private String phone;
    private Integer status;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
