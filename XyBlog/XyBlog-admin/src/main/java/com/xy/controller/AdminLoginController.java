package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.LoginUser;
import com.xy.domain.entity.Menu;
import com.xy.domain.entity.User;
import com.xy.domain.vo.AdminUserInfoVo;
import com.xy.domain.vo.RoutersVo;
import com.xy.domain.vo.UserInfoVo;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.service.AdminLoginService;
import com.xy.service.BlogLoginService;
import com.xy.service.MenuService;
import com.xy.service.RoleService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminLoginController {

    @Autowired
    private AdminLoginService adminLoginService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;


    //-------------用户登录接口-------------
    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        //前端也可以做一些校验
        //但@PostMapping("/login")是为了防止某些牛逼的人直接伪造http请求来访问后端
        //因此 后端也需要做校验
        if(!StringUtils.hasText(user.getUserName())){
            //提示 必须要传用户名
            // throw new RuntimeException();  // 以前一直都用RuntimeException来抛出异常  后面的话就需要自定义异常来抛出异常
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);//即必须提醒用户名
        }
        return adminLoginService.login(user);
    }
    //-------------用户登出接口-------------
    @PostMapping("/user/logout")
    public ResponseResult logout(){
        return adminLoginService.logout();
    }

    /**
     * 在SpringSecurity的课程中介绍过RBAC权限模型。
     * 这里就是在RBAC权限模型的基础上去实现这个功能。
     * M菜单->C目录->F按钮
     * @return
     */
    //-----------后台系统需要能实现不同的用户权限可以看到不同的功能-----------
    //-----------------用户只能使用他的权限所允许使用的功能------------------
    //-------------------只有C目录 F按钮涉及到权限的设置--------------------
    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo(){
        //获取当前登录的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //根据用户id查询权限信息
        List<String> perms = menuService.selectPermsByUserId(loginUser.getUser().getId());
        //根据用户id查询角色信息
        List<String> roleKeyList = roleService.selectRoleKeyByUserId(loginUser.getUser().getId());
        //封装数据并返回
        User user = loginUser.getUser();
        UserInfoVo userInfoVo=BeanCopyUtils.copyBean(user, UserInfoVo.class);
        AdminUserInfoVo adminUserInfoVo =new AdminUserInfoVo(perms,roleKeyList,userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);
    }
    //----前端为了实现动态路由的效果，需要后端有接口能返回用户所能访问的菜单数据----
    // ----------------返回的菜单数据需要体现父子菜单的层级关系-------------------
    //-------------------只有M菜单 C目录涉及到菜单的设置--------------------
    @GetMapping("/getRouters")
    public ResponseResult<RoutersVo> getRouters(){
        Long userId = SecurityUtils.getUserId();
        //查询menu 结果是tree的形式
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(userId);
        //封装并返回
        return ResponseResult.okResult(new RoutersVo(menus));
    }
}
