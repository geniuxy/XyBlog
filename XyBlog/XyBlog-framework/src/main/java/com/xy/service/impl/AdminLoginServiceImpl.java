package com.xy.service.impl;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.LoginUser;
import com.xy.domain.entity.User;
import com.xy.domain.vo.BlogUserLoginVo;
import com.xy.domain.vo.UserInfoVo;
import com.xy.service.AdminLoginService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.JwtUtil;
import com.xy.utils.RedisCache;
import com.xy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 后台的登录和前台的登录相比
 * 区别是
 * 后台只需要封装token 不需要用户信息
 * 而前台需要把token和userinfo封装起来并返回
 */
@Service
public class AdminLoginServiceImpl implements AdminLoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;

    /**
     * 登录管理员用户
     * @param user
     * @return
     */
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
        redisCache.setCacheObject("adminlogin:"+userId,loginUser);

        //把token和userinfo封装起来 并返回
        //先把user转化成UserInfoVo
//        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
//        BlogUserLoginVo vo=new BlogUserLoginVo(jwt,userInfoVo);
//        return ResponseResult.okResult(vo);
        //---------以上是前台后端代码---------
        //把token封装起来并返回
        //响应体 “code”+“msg”+“data”里有个“token”
        Map<String,String> map =new HashMap<>();
        map.put("token",jwt);
        return ResponseResult.okResult(map);
    }

    /**
     * 退出登录管理员用户
     * @return
     */
    @Override
    public ResponseResult logout() {
        //获取当前登录的用户id
        Long userId = SecurityUtils.getUserId();
        //删除redis中对应的值
        redisCache.deleteObject("adminlogin:"+userId);
        return ResponseResult.okResult();
    }

}
