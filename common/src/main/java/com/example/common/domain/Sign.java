package com.example.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sign {
    private Long id;
    private Long expirationTime;
}
