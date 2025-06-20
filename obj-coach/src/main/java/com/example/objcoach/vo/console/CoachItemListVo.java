package com.example.objcoach.vo.console;

import lombok.Data;

import java.util.List;

@Data
public class CoachItemListVo {
    private List<CoachItemVo> list;
    private Integer pageSize;
    private Integer total;
}
