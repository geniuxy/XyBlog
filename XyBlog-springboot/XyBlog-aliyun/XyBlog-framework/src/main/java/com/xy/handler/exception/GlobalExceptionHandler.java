package com.xy.handler.exception;

import com.xy.domain.ResponseResult;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */

//@ControllerAdvice：controller增强+@ResponseBody:结果放到响应体中
//@RestControllerAdvice=@ControllerAdvice+@ResponseBody

@RestControllerAdvice
@Slf4j//lombok提供的日志打印
public class GlobalExceptionHandler{
    //这个方法用来处理SystemException的异常处理
    //SystemException是自定义的异常类
    @ExceptionHandler(SystemException.class)
    public ResponseResult systemExceptionHandler(SystemException e){
        //打印异常信息 方便做一个分析
        log.error("出现了异常！{}",e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(e.getCode(),e.getMsg());
    }

    @ExceptionHandler(Exception.class)//这个方法用来处理Exception除了SystemException下除了的异常处理
    public ResponseResult exceptionHandler(Exception e){
        //打印异常信息 方便做一个分析
        log.error("出现了异常！{}",e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),e.getMessage());
    }

}
