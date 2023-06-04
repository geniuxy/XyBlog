package com.xy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.domain.entity.User;
import com.xy.domain.entity.UserRole;
import com.xy.mapper.UserRoleMapper;
import com.xy.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户和角色关联表(UserRole)表服务实现类
 *
 * @author Xy
 * @since 2023-06-02 17:04:24
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public void deleteUserRoleByRoleId(Long roleId) {
        getBaseMapper().deleteByRoleId(roleId);
    }

    /**
     * 插入用户的角色信息
     * @param user
     */
    @Override
    public void insertUserRole(User user) {
        for (Long roleId : user.getRoleIds()) {
            save(new UserRole(user.getId(),roleId));
        }
    }

    @Override
    public void deleteUserRoleByUserId(String userId) {
        getBaseMapper().deleteByUserId(userId);
    }
}

