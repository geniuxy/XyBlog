package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author Xy
 * @since 2023-05-31 16:10:49
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long id);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    List<Menu> selectMenuList(Menu menu);

    List<Long> selectMenuListByRoleId(Long roleId);

    ResponseResult addMenu(Menu menu);

    ResponseResult getMenuById(Long id);


    boolean hasChild(Long menuId);

    void removeMenuById(Long menuId);
}

