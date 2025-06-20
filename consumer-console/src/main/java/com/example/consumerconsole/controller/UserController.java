package com.example.consumerconsole.controller;

import com.alibaba.fastjson.JSON;
import com.example.common.domain.Response;
import com.example.common.domain.Sign;
import com.example.consumerconsole.feign.UserFeign;
import com.example.objuser.entity.User;
import com.example.objuser.vo.UserVo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserFeign userFeign;

    @RequestMapping("/user/login")
    public Response<UserVo> login(@RequestParam("phone") String phone, @RequestParam("password") String password, HttpServletResponse response) {
        Response<User> result = userFeign.getByPhone(phone);
        if (1001 != result.getCode()) {
            return new Response<>(result.getCode());
        }

        User user = result.getData();
        if (null == user) {
            return new Response<>(2001);
        }

        // 检查原始密码和存储的哈希值是否匹配。返回true或false。
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return new Response<>(1003);
        }

        // 构建sign
        Sign sign = new Sign(user.getId(), System.currentTimeMillis() + 3600000L);  // 一小时以后
        String jsonString = JSON.toJSONString(sign);  // 实体转json
        String signString = Base64.getUrlEncoder().encodeToString(jsonString.getBytes());

        // 构建cookie
        Cookie cookie = new Cookie("sign", signString);
        cookie.setMaxAge(60 * 60); // 设置Cookie的过期时间为1个小时，如果sign过期，那么这个cookie也就没有意义了
        cookie.setPath("/"); // 设置Cookie的作用路径
        response.addCookie(cookie); // 将Cookie添加到响应中

        UserVo userVo = new UserVo(user.getName(), user.getPhone(), user.getAvatar(), signString);
        return new Response<>(1001, userVo);
    }
}
