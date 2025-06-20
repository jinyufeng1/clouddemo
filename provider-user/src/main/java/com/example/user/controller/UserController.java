package com.example.user.controller;

import com.example.common.domain.Response;
import com.example.objuser.dto.EditUserDTO;
import com.example.objuser.entity.User;
import com.example.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    public Response<Long> add(@RequestParam(name = "name", required = false) String name, @RequestParam("password") String password,
                              @RequestParam(name = "phone") String phone , @RequestParam(name = "avatar", required = false) String avatar) {
        Long userId = userService.edit(new EditUserDTO(null, name, password, phone, avatar));
        return new Response<>(1001, userId);
    }

    @RequestMapping("/getByPhone")
    public Response<User> getByPhone(@RequestParam("phone") String phone) {
        User user = userService.getByPhone(phone);
        return new Response<>(1001, user);
    }

    @RequestMapping("/getById")
    public Response<User> getById(@RequestParam("id") Long id) {
        User user = userService.getById(id);
        return new Response<>(1001, user);
    }
}
