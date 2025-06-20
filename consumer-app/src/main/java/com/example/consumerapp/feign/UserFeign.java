package com.example.consumerapp.feign;

import com.example.common.domain.Response;
import com.example.objuser.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user")
public interface UserFeign {
    @RequestMapping("/user/add")
    Response<Long> add(@RequestParam(name = "name", required = false) String name, @RequestParam("password") String password,
                       @RequestParam(name = "phone") String phone , @RequestParam(name = "avatar", required = false) String avatar);
    @RequestMapping("/user/getById")
    Response<User> getById(@RequestParam("id") Long id);

    @RequestMapping("/user/getByPhone")
    Response<User> getByPhone(@RequestParam("phone") String phone);
}
