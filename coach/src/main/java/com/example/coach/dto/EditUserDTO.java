package com.example.coach.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class EditUserDTO {
    private Long id;
    private String name;
    private String password;
    private String phone;
    private String avatar;
}
