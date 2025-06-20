package com.example.consumerapp.verification.login;

import com.alibaba.fastjson.JSON;
import com.example.common.domain.Response;
import com.example.common.domain.Sign;
import com.example.consumerapp.feign.UserFeign;
import com.example.consumerapp.verification.login.annotations.VerifiedUser;
import com.example.consumerapp.verification.login.entity.CurrentUser;
import com.example.objuser.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.Base64;

// 自定义的HandlerMethodArgumentResolver会在内置解析器之后注册，调用时按注册顺序执行，如果内置参数解析都没有被执行，就会尝试这个自定义处理器方法参数解析器
@Slf4j
public class UserAuthorityResolver implements HandlerMethodArgumentResolver {

    @Autowired
    @Lazy
    private UserFeign userFeign;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> type = parameter.getParameterType();
        return type.isAssignableFrom(CurrentUser.class) && parameter.hasParameterAnnotation(VerifiedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory factory) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> parameterType = parameter.getParameterType();
        CurrentUser currentUser = (CurrentUser) parameterType.getDeclaredConstructor().newInstance();

        HttpServletRequest sRequest = (HttpServletRequest)request.getNativeRequest();
        String signStr = sRequest.getHeader("SignKey");
        // 未登录
        if(null == signStr) {
            currentUser.setCode(1002);
            return currentUser;
        }

        //Base64解码
        String decode = new String(Base64.getUrlDecoder().decode(signStr));
        //获取json转实体
        Sign sign = JSON.parseObject(decode, Sign.class);

        // 登录超时
        Long expirationTime = sign.getExpirationTime();
        if (expirationTime < System.currentTimeMillis()) {
            currentUser.setCode(1004);
            return currentUser;
        }

        Response<User> userResponse = userFeign.getById(sign.getId());
        Integer code = userResponse.getCode();
        if (1001 != code) {
            currentUser.setCode(code);
            return currentUser;
        }

        User userInfo = userResponse.getData();
        // 未注册
        if (ObjectUtils.isEmpty(userInfo)) {
            currentUser.setCode(1005);
            return currentUser;
        }

        currentUser.setUserInfo(userInfo);
        currentUser.setCode(1001);
        return currentUser;
    }
}
