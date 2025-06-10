package com.example.coach.entity;

import lombok.Data;

/**
 * <p>
 * 教练标签关联表
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-28
 */
@Data
public class RelationTagCoach {
    private Long id;
    private Long coachId;
    private Long tagId;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
