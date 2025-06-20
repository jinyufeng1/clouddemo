package com.example.objcoach.vo.app;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WpVo {
    private Integer page;
    private String keyword;
    private String firstTime;
    List<Long> leafCategoryIds;
}
