package com.example.consumerconsole.verification.login;

import com.alibaba.fastjson.JSON;
import com.example.common.domain.Response;
import com.example.common.domain.Sign;
import com.example.consumerconsole.feign.UserFeign;
import com.example.objuser.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Base64;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    //@Lazy 解决循环依赖问题
    @Autowired
    @Lazy
    private UserFeign userFeign;

    // 通过这样的方式在拦截器返回Response
    private void writeResponse(HttpServletResponse response, Integer code) throws IOException {
        Response objectResponse = new Response<>(code);
        response.setContentType("application/json;charset=UTF-8"); // 设置内容类型和字符编码
        String jsonString = JSON.toJSONString(objectResponse);
        response.getWriter().write(jsonString);
    }

    // 返回 true 表示继续处理请求，返回 false 表示中断请求
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 登录判断
        Cookie[] cookies = request.getCookies();
        String signStr = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sign".equals(cookie.getName())) {
                    signStr = cookie.getValue();
                    break;
                }
            }
        }

        if(null == signStr) {
            writeResponse(response, 1002);
            return false;
        }

        //Base64解码
        String decode = new String(Base64.getUrlDecoder().decode(signStr));
        //获取json转实体
        Sign sign = JSON.parseObject(decode, Sign.class);

        Long expirationTime = sign.getExpirationTime();
        if (expirationTime < System.currentTimeMillis()) {
            writeResponse(response, 1004);
            return false;
        }

        Response<User> userResponse = userFeign.getById(sign.getId());
        Integer code = userResponse.getCode();
        if (1001 != code) {
            writeResponse(response, code);
            return false;
        }

        if (ObjectUtils.isEmpty(userResponse.getData())) {
            writeResponse(response, 1005);
            return false;
        }

        return true;
    }
}