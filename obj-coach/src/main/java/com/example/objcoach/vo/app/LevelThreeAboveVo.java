package com.example.objcoach.vo.app;

import lombok.Data;

import java.util.List;

@Data
public class LevelThreeAboveVo {
    // 子节点
    private List<CategoryItemVo> categoryItems;

    // 三级以上类目下的推荐列表
    private List<CoachItemVo> coachItems;

    private Boolean isEnd;

    private String wp;
}
