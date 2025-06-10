package com.example.common.domain;

import lombok.Data;

@Data
public class ImageVo {
    private String src;
    private Float ar = 1.0f; // 默认值0.1
}
