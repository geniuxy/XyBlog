package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.dto.UserDto;
import com.xy.domain.entity.User;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.certpath.SunCertPathBuilderException;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表
     *
     * @param pageNum
     * @param pageSize
     * @param userDto
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getUserList(Integer pageNum, Integer pageSize, UserDto userDto) {
        return userService.getUserList(pageNum, pageSize, userDto);
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @PostMapping
    public ResponseResult addUser(@RequestBody User user) {
        if (!StringUtils.hasText(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (!userService.checkUserNameUnique(user)) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (!userService.checkPhoneUnique(user)) {
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        if (!userService.checkEmailUnique(user)) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        return userService.addUser(user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }

    /**
     * 根据id查询用户信息回显接口
     */
    @GetMapping("/{id}")
    public ResponseResult getUserDetails(@PathVariable Long id) {
        return userService.getUserDetails(id);
    }

    /**
     * 更新用户
     */
    @PutMapping
    public ResponseResult updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseResult.okResult();
    }

}
