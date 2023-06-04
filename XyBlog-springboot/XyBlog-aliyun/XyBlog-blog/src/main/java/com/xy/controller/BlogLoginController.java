package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.User;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.service.BlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogLoginController {
    @Autowired
    private BlogLoginService blogLoginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        //前端也可以做一些校验
        //但@PostMapping("/login")是为了防止某些牛逼的人直接伪造http请求来访问后端
        //因此 后端也需要做校验
        if(!StringUtils.hasText(user.getUserName())){
            //提示 必须要传用户名
            // throw new RuntimeException();  // 以前一直都用RuntimeException来抛出异常  后面的话就需要自定义异常来抛出异常
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);//即必须提醒用户名
        }
        return blogLoginService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult loginout(){
        return blogLoginService.loginout();
    }


}

