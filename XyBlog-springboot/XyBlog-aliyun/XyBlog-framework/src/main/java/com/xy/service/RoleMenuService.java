package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.entity.RoleMenu;


/**
 * 角色和菜单关联表(RoleMenu)表服务接口
 *
 * @author Xy
 * @since 2023-06-02 15:18:56
 */
public interface RoleMenuService extends IService<RoleMenu> {

    void deleteRoleMenuByRoleId(Long id);

    void deleteRoleMenuByMenuId(Long menuId);
}

