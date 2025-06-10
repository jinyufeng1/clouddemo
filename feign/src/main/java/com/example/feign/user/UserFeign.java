package com.example.feign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user")
public interface UserFeign {
    @RequestMapping("/feign/check/sign")
    Integer checkSign(@RequestParam String signStr);
}
