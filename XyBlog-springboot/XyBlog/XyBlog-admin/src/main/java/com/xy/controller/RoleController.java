package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.dto.RoleStatusDto;
import com.xy.domain.entity.Role;
import com.xy.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    public ResponseResult getRoleList(Integer pageNum,Integer pageSize,Role role){
        return roleService.getRoleList(pageNum,pageSize,role);
    }

    /**
     * 新增角色接口
     */
    @PostMapping
    public ResponseResult addRole(@RequestBody Role role){
        return roleService.addRole(role);
    }
    /**
     * 更改角色状态
     * @param roleStatusDto
     * @return
     */
    @PutMapping("/changeStatus")
    public ResponseResult changeRoleStatus(@RequestBody RoleStatusDto roleStatusDto){
        Role role=new Role();
        role.setId(roleStatusDto.getRoleId());
        role.setStatus(roleStatusDto.getStatus());
        return ResponseResult.okResult();
    }

    /**
     * 角色信息回显接口
     */
    @GetMapping("/{id}")
    public ResponseResult getRoleDetails(@PathVariable Long id){
        return ResponseResult.okResult(roleService.getById(id));
    }

    /**
     *更新角色信息接口
     */
    @PutMapping
    public ResponseResult updateRole(@RequestBody Role role){
        return roleService.updateRole(role);
    }
    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteRole(@PathVariable String id){
        return roleService.deleteRole(id);
    }
    /**
     * 查询角色列表接口
     * 用于后台创建新用户时使用
      */
    @GetMapping("/listAllRole")
    public ResponseResult getAllRoleList(){
        List<Role> roles = roleService.selectRoleAll();
        return ResponseResult.okResult(roles);
    }
}
