package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Menu;
import com.xy.domain.vo.MenuTreeVo;
import com.xy.domain.vo.MenuVo;
import com.xy.domain.vo.RoleMenuTreeSelectVo;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.service.MenuService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.SystemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    public ResponseResult getMenuList(Menu menu){
        //得到所有菜单
        List<Menu> menus=menuService.selectMenuList(menu);
        //封装结果
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menus, MenuVo.class);
        return ResponseResult.okResult(menuVos);
    }

    /**
     * 新增菜单
     */
    //为什么@GetMapping方法的参数里没有@RequestBody，而@PostMapping里有呢！?
    //这是因为get请求的参数是在url里的！
    @PostMapping//post是新增
    public ResponseResult addMenu(@RequestBody Menu menu){
        return menuService.addMenu(menu);
    }

    /**
     * 根据id查询菜单数据
     * @param id
     * @return
     */
    @GetMapping ("/{id}")
    public ResponseResult getMenuById(@PathVariable Long id){
        return menuService.getMenuById(id);
    }

    /**
     * 更改菜单数据
     * @param menu
     * @return
     */
    @PutMapping//put是更新
    public ResponseResult updateMenu(@RequestBody Menu menu){
        //如果修改父级菜单是自己 就会报错
        if (menu.getId().equals(menu.getParentId())) {
            return ResponseResult.errorResult(500,"修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menuService.updateById(menu);
        return ResponseResult.okResult();
    }


    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public ResponseResult removeMenuById(@PathVariable Long menuId) {
        //如果存在子菜单就不可以删除
        if (menuService.hasChild(menuId)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.EXIST_CHILDREN_MENU);
        }
        menuService.removeMenuById(menuId);
        return ResponseResult.okResult();
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public ResponseResult treeselect() {
        //复用之前的selectMenuList方法。方法需要参数，参数可以用来进行条件查询，而这个方法不需要条件，所以直接new Menu()传入
        List<Menu> menus = menuService.selectMenuList(new Menu());
        List<MenuTreeVo> options = SystemConverter.buildMenuSelectTree(menus);
        return ResponseResult.okResult(options);
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public ResponseResult roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        List<Menu> menus = menuService.selectMenuList(new Menu());
        //checkedKeys角色所关联的菜单权限id列表
        List<Long> checkedKeys = menuService.selectMenuListByRoleId(roleId);
        List<MenuTreeVo> menuTreeVos = SystemConverter.buildMenuSelectTree(menus);
        RoleMenuTreeSelectVo vo = new RoleMenuTreeSelectVo(checkedKeys,menuTreeVos);
        return ResponseResult.okResult(vo);
    }
}
