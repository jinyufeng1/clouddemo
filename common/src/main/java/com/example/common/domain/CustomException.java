package com.example.common.domain;

import lombok.Data;

@Data
public class CustomException extends RuntimeException {

    Integer code;
    public CustomException(Integer code) {
        this.code = code;
    }
}
