package com.example.user.feign.controller;

import com.alibaba.fastjson.JSON;
import com.example.user.Sign;
import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/feign")
public class FeignController {
    @Autowired
    private UserService userService;

    @RequestMapping("/check/sign")
    public Integer checkSign(@RequestParam String signStr) {
        //Base64解码
        String decode = new String(Base64.getUrlDecoder().decode(signStr));
        //获取json转实体
        Sign sign = JSON.parseObject(decode, Sign.class);

        Long expirationTime = sign.getExpirationTime();
        if (expirationTime < System.currentTimeMillis()) {
            return 1004;
        }

        if (ObjectUtils.isEmpty(userService.getById(sign.getId()))) {
            return 1005;
        }

        return 1001;
    }
}
