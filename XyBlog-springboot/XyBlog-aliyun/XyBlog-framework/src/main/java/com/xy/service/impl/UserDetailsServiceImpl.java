package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xy.constants.SystemConstants;
import com.xy.domain.entity.LoginUser;
import com.xy.domain.entity.User;
import com.xy.mapper.MenuMapper;
import com.xy.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    //因为 @Mapper 这个注解是 Mybatis 提供的，而 @Autowried 注解是 Spring 提供的，IDEA大概是能识别 Spring 的上下文，
    //但是不可以识别 Mybatis 的吧。根据 @Autowried 源码看到@Autowried 要求依赖对象必须存在，那么此时 IDEA 只能给个红色警告了
    //不改也不会影响运行 我还是改一下吧  @Autowired(required = false)
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    //因为UserDetailsService是去内存中查询用户信息，所以我们可以自定义loadUserByUsername方法去数据库中查询用户信息
    //封装为UserDetails对象并返回
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户信息
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<User>();
        queryWrapper.eq(User::getUserName,username);
        User user = userMapper.selectOne(queryWrapper);
        //判断是否查到用户  如果没查到抛出异常
        if(Objects.isNull(user)){
            throw new RuntimeException("用户不存在");
        }
        //返回用户信息
        //查询权限信息封装(后面后台部分了来完善)
        //如果是后台用户才需要查询权限封装
        if(user.getType().equals(SystemConstants.ADMAIN)){
            List<String> list = menuMapper.selectPermsByUserId(user.getId());
            return new LoginUser(user,list);
        }
        //普通用户的权限
        return new LoginUser(user,null);
    }
}
