package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.dto.UserDto;
import com.xy.domain.entity.User;


/**
 * 用户表(User)表服务接口
 *
 * @author Xy
 * @since 2023-05-28 20:47:37
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

    ResponseResult getUserList(Integer pageNum, Integer pageSize, UserDto userDto);

    boolean checkUserNameUnique(User user);

    boolean checkPhoneUnique(User user);

    boolean checkEmailUnique(User user);

    ResponseResult addUser(User user);

    ResponseResult deleteUser(String id);

    ResponseResult getUserDetails(Long id);

    void updateUser(User user);
}

