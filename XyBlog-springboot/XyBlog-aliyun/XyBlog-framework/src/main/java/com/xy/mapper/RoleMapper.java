package com.xy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.domain.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author Xy
 * @since 2023-05-31 16:11:06
 */
public interface RoleMapper extends BaseMapper<Role> {

    List<String> selectRoleKeyByUserId(Long userId);

    void updatedelFlagById(List<Long> roleIds);

    List<Long> selectRoleIdByUserId(Long userId);
}
