package com.example.feign;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {
    // 使用 OpenFeign 本身不会直接导致这个问题，但当浏览器调用接口时，OpenFeign 的日志可能会显示额外的 /favicon 请求，从而让人误以为是 OpenFeign 导致的
    @GetMapping("/favicon.ico")
    @ResponseBody
    public String favicon() {
        return ""; // 返回空响应
    }
}