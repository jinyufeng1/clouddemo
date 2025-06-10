package com.example.webcommon;

import com.example.common.domain.CustomException;
import com.example.common.domain.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception ex) {
        ex.printStackTrace();

        int code = 3001;
        if (ex instanceof CustomException) {
            code = ((CustomException)ex).getCode();
        }

        return new Response(code);
    }
}
