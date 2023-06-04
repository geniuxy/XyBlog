package com.xy.handler.security;

import com.alibaba.fastjson.JSON;
import com.xy.domain.ResponseResult;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.utils.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义 认证失败处理器 的实现类
 * 配置完之后需要在SecurityConfig中配置异常处理器
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //异常信息打印一下
        authException.printStackTrace();
        //封装错误信息  这边不同的请求有着不同的错误码
        //InsufficientAuthenticationException   别的请求未携带token时出的错
        //BadCredentialsException   登录时用户名或者密码错误
        ResponseResult result =null;
        if(authException instanceof BadCredentialsException){
            result = ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_ERROR.getCode(),authException.getMessage());
        }else if(authException instanceof InsufficientAuthenticationException){
            result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }else {//如果是其他错误
            result=ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),"认证或授权失败");
        }
        //响应给前端
        WebUtils.renderString(response, JSON.toJSONString(result));
    }
}
