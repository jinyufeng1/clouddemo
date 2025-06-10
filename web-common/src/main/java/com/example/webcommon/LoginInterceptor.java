package com.example.webcommon;

import com.alibaba.fastjson.JSON;
import com.example.common.domain.Response;
import com.example.feign.user.UserFeign;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

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

        Integer code = userFeign.checkSign(signStr);
        if (code == 1001) {
            return true;
        }

        writeResponse(response, code);
        return false;
    }
}