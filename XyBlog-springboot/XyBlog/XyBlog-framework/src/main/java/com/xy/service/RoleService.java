package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author Xy
 * @since 2023-05-31 16:11:06
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult addRole(Role role);

    ResponseResult updateRole(Role role);

    ResponseResult getRoleList(Integer pageNum, Integer pageSize, Role role);

    ResponseResult deleteRole(String id);

    List<Role> selectRoleAll();

    List<Long> selectRoleIdByUserId(java.lang.Long id);
}

