package com.xy.aspect;

import com.alibaba.fastjson.JSON;
import com.xy.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 切面类：用来自定义打印日志信息格式
 */
@Component//进入IOC容器
@Aspect//切面
@Slf4j//用了这个注解就可以打印日志 使用log对象
public class LogAspect {
    // 要做两件事：
    // 1.确定切点
    @Pointcut("@annotation(com.xy.annotation.SystemLog)")//定义切点：所有用的这个注解的方法都要实现日志打印
    public void pt() {}
    // 2.通知方法,增强代码
    @Around("pt()")//@Around 环绕通知 在方法前 方法后 都可以进行增强
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {//这个joinPoint就相当于被增强方法的信息
        //需要final来打印日志打印结束的信息
        //proceed()可能会异常，所以在方法声明中抛出异常
        //不是try-catch-final是因为catch了异常就会使之前配置的统一异常配置失效
        //在try里面打印之前需要的信息
        Object ret;
        try {
            handleBefore(joinPoint);//打印初始信息 请求信息
            ret = joinPoint.proceed();//让注解的方法去运行
            handleAfter(ret);//打印响应信息
        } finally {
            // 结束后换行
            log.info("=======End=======" + System.lineSeparator());//System.lineSeparator()当前运行系统的换行符
        }
        return ret;
    }

    /**
     * 打印初始信息 请求信息
     *
     * @param joinPoint
     */
    private void handleBefore(ProceedingJoinPoint joinPoint) {
        //一般...Holder都是用ThreadLocal来记录数据
        //保证在多线程的环境下 数据之间不会影响
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();//用于获取请求对象
        //RequestContextHolder是个接口，所以要调用他的实现类 Ctrl+Alt+左键 即可以找出它的实现类
        //并从中找出可以得到Request的实现类
        HttpServletRequest request = requestAttributes.getRequest();

        //获取被增强方法上的注解对象
        SystemLog systemLog = getSystemLog(joinPoint);

        //日志打印开始
        log.info("=======Start=======");
        // 打印请求 URL
        // URL在request中
        log.info("URL            : {}", request.getRequestURL());
        // 打印描述信息
        log.info("BusinessName   : {}", systemLog.businessName());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        log.info("IP             : {}", request.getRemoteHost());//RemoteHost -> 请求的 IP
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    /**
     * 打印响应信息
     */
    private void handleAfter(Object ret) {
        // 打印出参
        // 因为返回结果是ResponseResult
        // 只要将响应JSON结果用JSON工具转化为String即可
        log.info("Response       : {}", JSON.toJSONString(ret));
    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        //MethodSignature是Signature的一个子接口(就是不停的extends的那种关系)
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //就是把方法接口(controller层文件里的那些方法)封装成一个签名
        //签名里除了method还有别的信息 所以要getMethod
        //再获取注解getAnnotation(SystemLog.class)
        SystemLog systemLog = methodSignature.getMethod().getAnnotation(SystemLog.class);
        //即获得SystemLog对象 我们自定义的注解对象
        return systemLog;
    }

}
