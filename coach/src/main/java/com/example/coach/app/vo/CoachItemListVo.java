package com.example.coach.app.vo;

import lombok.Data;

import java.util.List;

@Data
public class CoachItemListVo {
    private List<CoachItemVo> list;
    private Boolean isEnd;
    private String wp;
}
