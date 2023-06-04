package com.xy.handler.security;

import com.alibaba.fastjson.JSON;
import com.xy.domain.ResponseResult;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.utils.WebUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义 授权失败处理器 的实现类
 * 配置完之后需要在SecurityConfig中配置异常处理器
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //异常信息打印一下
        accessDeniedException.printStackTrace();
        //封装错误信息
        ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        //响应给前端
        WebUtils.renderString(response, JSON.toJSONString(result));
    }
}
