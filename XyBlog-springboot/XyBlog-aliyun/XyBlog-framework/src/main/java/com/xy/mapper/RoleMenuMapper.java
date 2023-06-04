package com.xy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.domain.entity.RoleMenu;


/**
 * 角色和菜单关联表(RoleMenu)表数据库访问层
 *
 * @author Xy
 * @since 2023-06-02 15:18:55
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    void removeByRoleId(Long roleId);

    void removeByMenuId(Long menuId);
}
