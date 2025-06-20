package com.example.consumerconsole.feign;

import com.example.common.domain.Response;
import com.example.objuser.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user")
public interface UserFeign {
    @RequestMapping("/user/getById")
    Response<User> getById(@RequestParam("id") Long id);

    @RequestMapping("/user/getByPhone")
    Response<User> getByPhone(@RequestParam("phone") String phone);
}
