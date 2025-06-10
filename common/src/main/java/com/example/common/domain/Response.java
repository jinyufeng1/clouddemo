package com.example.common.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data //转json字符串时需要
public class Response<T> {

    private Integer code;
    private String msg;
    private T data;

    // 只体现业务相关问题给用户，技术相关的程序员在日志里看
    public static Map<Integer, String> map = new HashMap<Integer, String>() {{
        put(1001, "OK");
        put(1002, "需要登录");
        put(1003, "登录密码错误，请核对后重新填写");
        put(1004, "登录已超时");
        put(1005, "未注册此用户");

        put(2001, "手机号并不存在");
        put(2002, "手机号重复");
        put(2003, "短信发送失败");

        put(3001, "服务器异常，如需处理请联系技术人员");

        put(4003, "权限不足");
        put(4004, "网络繁忙");

        put(5001, "解析失败，上传文件非excel文件");
    }};

    public Response(Integer code) {
        this.code = code;
        this.msg = map.get(code);
    }

    public Response(Integer code, T data) {
        this.code = code;
        this.msg = map.get(code);
        this.data = data;
    }

}
