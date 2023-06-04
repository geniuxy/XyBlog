package com.xy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.domain.entity.UserRole;


/**
 * 用户和角色关联表(UserRole)表数据库访问层
 *
 * @author Xy
 * @since 2023-06-02 17:04:23
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    void deleteByRoleId(Long roleId);

    void deleteByUserId(String userId);
}
