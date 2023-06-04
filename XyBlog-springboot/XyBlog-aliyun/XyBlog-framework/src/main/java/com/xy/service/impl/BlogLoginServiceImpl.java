package com.xy.service.impl;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.LoginUser;
import com.xy.domain.entity.User;
import com.xy.domain.vo.BlogUserLoginVo;
import com.xy.domain.vo.UserInfoVo;
import com.xy.service.BlogLoginService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.JwtUtil;
import com.xy.utils.RedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult login(User user) {
        //把登入用户的名称和密码封装到这个authenticationToken，其中Authentication是UsernamePasswordAuthenticationToken的父类
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或者密码错误");
        }
        //获取userid 生成token (jwt == java web token)
        LoginUser loginUser=(LoginUser)authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("bloglogin:"+userId,loginUser);

        //把token和userinfo封装起来 并返回
        //先把user转化成UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo vo=new BlogUserLoginVo(jwt,userInfoVo);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult loginout() {
        // 获取token 解析获取Loginuser
        // 登录的时候把token放到了SecurityContextHolder里
        // SecurityContextHolder内部使用threadLocal
        // 在使用 ThreadLocal 存储数据时，不需要对数据加锁或者使用同步方法
        // 因为每个线程都会单独维护一份数据，互不干扰。
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 获取userid
        Long userId = loginUser.getUser().getId();
        // 删除redis中的用户信息
        redisCache.deleteObject("bloglogin:"+userId);

        return ResponseResult.okResult();
    }
}
