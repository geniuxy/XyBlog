package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Menu;
import com.xy.domain.vo.MenuVo1;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.mapper.MenuMapper;
import com.xy.service.MenuService;
import com.xy.service.RoleMenuService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author Xy
 * @since 2023-05-31 16:10:49
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 根据用户id查询权限信息
     * @param id
     * @return
     */
    @Override
    public List<String> selectPermsByUserId(Long id) {
        //如果是管理员，返回所有的权限
        if(SecurityUtils.isAdmin()){//管理员就是1号选手！
            LambdaQueryWrapper<Menu> wrapper =new LambdaQueryWrapper<>();
            //queryWrapper涉及多个值时，用in
            //菜单类型为“C"和“F”
            wrapper.in(Menu::getMenuType, SystemConstants.MENU,SystemConstants.BUTTON);//菜单、按钮
            //状态是正常的
            wrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(wrapper);
            //获取menus里的权限标识
            List<String> perms = menus.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        //否则返回其所具有的权限
        //先role表里查用户是什么角色
        //再查角色对应的权限
        return getBaseMapper().selectPermsByUserId(id);
        //去实现类的BaseMapper里，也就是MenuMapper里，查
        //用的也叫这个方法 不过是在MenuMapper里
        //查用户对应角色的权限
    }

    /**
     *  得到能够访问菜单树(父子关系)
     * @param userId
     * @return
     */
    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        //判断是否是管理员
        if(SecurityUtils.isAdmin()){
            //如果是 ->返回所有符合要求的Menu
            menus = menuMapper.selectAllRouterMenu();

        }else{
            //否则 当前用户所具有的Menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }
        //构件树
        //先找出第一层菜单 然后再去找他们的子菜单设置到children属性中
        List<Menu> menuTree = buildMenuTree(menus,0L);
        return menuTree;
    }


    /**
     * 构建有层级关系的菜单树
     *
     * @param menus
     * @param parentId
     * @return
     */
    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))//过滤：得到parentId符合的menu
                .map(menu -> menu.setChildren(getChildren(menu, menus)))//这边用到了一个链式编程的思想，可以见Menu类
                .collect(Collectors.toList());
        return menuTree;
    }

    /**
     * 得到 Children 集合
     * 获取子菜单的集合并存入menus
     * 并且对应的children也要得到自己的children
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(m, menus)))
                .collect(Collectors.toList());
        return childrenList;
    }

    /**
     * 得到所有菜单列表
     * @param menu
     * @return
     */
    @Override
    public List<Menu> selectMenuList(Menu menu) {
        //先根据menuName和status进行查询
        LambdaQueryWrapper<Menu> queryWrapper =new LambdaQueryWrapper<>();
        //只有传来menuName和Status的时候才会进行判断
        queryWrapper.like(StringUtils.hasText(menu.getMenuName()),Menu::getMenuName,menu.getMenuName());
        queryWrapper.eq(StringUtils.hasText(menu.getStatus()),Menu::getStatus,menu.getStatus());
        //再按照父菜单id和orderNum进行排序
        queryWrapper.orderByAsc(Menu::getParentId,Menu::getOrderNum);
        List<Menu> menus = list(queryWrapper);
        return menus;
    }

    /**
     * 得到角色的菜单
     * @param roleId
     * @return
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return getBaseMapper().selectMenuListByRoleId(roleId);
    }

    @Override
    public ResponseResult addMenu(Menu menu) {
        //添加菜单
        save(menu);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getMenuById(Long id) {
        Menu menu = getById(id);
        MenuVo1 menuVo1 = BeanCopyUtils.copyBean(menu, MenuVo1.class);
        return ResponseResult.okResult(menuVo1);
    }

    /**
     * 判断菜单是否有子菜单
     * @param menuId
     * @return
     */
    @Override
    public boolean hasChild(Long menuId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,menuId);
        return count(queryWrapper) != 0;
    }

    @Override
    public void removeMenuById(Long menuId) {
        removeById(menuId);
        roleMenuService.deleteRoleMenuByMenuId(menuId);
    }

}

