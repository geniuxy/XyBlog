package com.xy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.domain.entity.RoleMenu;
import com.xy.mapper.RoleMenuMapper;
import com.xy.service.RoleMenuService;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表(RoleMenu)表服务实现类
 *
 * @author Xy
 * @since 2023-06-02 15:18:56
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    public void deleteRoleMenuByRoleId(Long roleId) {
        getBaseMapper().removeByRoleId(roleId);
    }

    @Override
    public void deleteRoleMenuByMenuId(Long menuId) {
        getBaseMapper().removeByMenuId(menuId);
    }
}

