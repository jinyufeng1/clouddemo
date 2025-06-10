package com.example.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sign {
    private Long id;
    private Long expirationTime;
}
