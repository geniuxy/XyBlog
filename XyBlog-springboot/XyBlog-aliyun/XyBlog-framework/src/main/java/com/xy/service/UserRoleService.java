package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.entity.User;
import com.xy.domain.entity.UserRole;


/**
 * 用户和角色关联表(UserRole)表服务接口
 *
 * @author Xy
 * @since 2023-06-02 17:04:24
 */
public interface UserRoleService extends IService<UserRole> {

    void deleteUserRoleByRoleId(Long roleId);

    void insertUserRole(User user);

    void deleteUserRoleByUserId(String id);
}

