package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Role;
import com.xy.domain.entity.RoleMenu;
import com.xy.domain.vo.PageVo;
import com.xy.domain.vo.RoleVo;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.mapper.RoleMapper;
import com.xy.service.RoleMenuService;
import com.xy.service.RoleService;
import com.xy.service.UserRoleService;
import com.xy.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author Xy
 * @since 2023-05-31 16:11:07
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 根据用户id查询角色信息
     * @param id
     * @return
     */
    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //判断是否是管理员 如果是返回集合中只需要返回roleKey=admin
        if(id==1L){
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //否则查询用户所具有的角色信息
        //要去系统里查
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    /**
     * 新增角色
     * @param role
     * @return
     */
    @Override
    @Transactional
    public ResponseResult addRole(Role role) {
        save(role);
        if(role.getMenuIds()!=null&&role.getMenuIds().length>0){
            insertRoleMenu(role);
        }
        return ResponseResult.okResult();
    }
    /**
     * 向role_menu表中添加role和menu的关系
     * @param role
     */
    private void insertRoleMenu(Role role) {
        List<RoleMenu> roleMenuList = Arrays.stream(role.getMenuIds())
                .map(memuId -> new RoleMenu(role.getId(), memuId))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(roleMenuList);
    }

    /**
     * 更新角色
     * @param role
     * @return
     */
    @Override
    @Transactional
    public ResponseResult updateRole(Role role) {
        updateById(role);
        roleMenuService.deleteRoleMenuByRoleId(role.getId());
        insertRoleMenu(role);
        return ResponseResult.okResult();
    }

    /**
     * 获取角色列表
     * @param pageNum
     * @param pageSize
     * @param role
     * @return
     */
    @Override
    public ResponseResult getRoleList(Integer pageNum, Integer pageSize, Role role) {
        //查询所有角色或符合条件的角色
        LambdaQueryWrapper<Role> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(role.getRoleName()),Role::getRoleName,role.getRoleName());
        queryWrapper.eq(StringUtils.hasText(role.getStatus()),Role::getStatus,role.getStatus());
        queryWrapper.orderByAsc(Role::getRoleSort);
        //分页
        Page<Role> page =new Page<Role>(pageNum,pageSize);
        page(page,queryWrapper);
        //封装数据
        List<Role> roles = page.getRecords();
        List<RoleVo> roleVos= BeanCopyUtils.copyBeanList(roles, RoleVo.class);
        PageVo pageVo =new PageVo(roleVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 批量删除角色
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteRole(String id) {
        String[] temps = id.split(",");
        List<Long> roleIds =new ArrayList<>();
        for (String temp:temps){
            Long roleId=Long.valueOf(temp);
            //删除role_Menu表中的对应的数据
            roleMenuService.deleteRoleMenuByRoleId(roleId);
            //删除user_role表中的对应的数据
            userRoleService.deleteUserRoleByRoleId(roleId);
            //role表中的del_flag是String 不用转换为Integer
            if (getById(roleId).getDelFlag().equals(SystemConstants.HAS_DELETED)) {
                throw new SystemException(AppHttpCodeEnum.HAS_DELETED);
            }//如果是别的状态码
            else if(!getById(roleId).getDelFlag().equals(SystemConstants.HAS_NOT_DELETED)){
                throw new SystemException(AppHttpCodeEnum.HAS_NOT_DELETED);
            }
            //遍历结果传入
            roleIds.add(roleId);
        }
        //剩下的就是此Tag未删除的情况
        //去执行更新del_Flag操作
        getBaseMapper().updatedelFlagById(roleIds);
        return ResponseResult.okResult();
    }

    /**
     * 查询创建用户时所有可选择的角色
     * @return
     */
    @Override
    public List<Role> selectRoleAll() {
        LambdaQueryWrapper<Role> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus,SystemConstants.NORMAL);
        List<Role> roles = list(queryWrapper);
        return roles;
    }

    @Override
    public List<Long> selectRoleIdByUserId(Long id) {
        return getBaseMapper().selectRoleIdByUserId(id);
    }

}

