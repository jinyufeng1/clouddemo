package com.example.coach.console.vo;

import com.example.coach.dto.BlockDTO;
import lombok.Data;

import java.util.List;

//详细信息
@Data
public class CoachDetailsVo {
//    轮播图
    private List<String> pics;

//    介绍
    private List<BlockDTO> intro;
//    分类
    private String category;

//    分类图标
    private String icon;

//    标签
    private List<String> tags;

//    创建时间
    private String createTime;

//    修改时间
    private String updateTime;
}
