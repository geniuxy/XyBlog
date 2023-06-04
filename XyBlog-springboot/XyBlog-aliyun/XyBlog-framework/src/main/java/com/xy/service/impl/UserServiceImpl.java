package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.dto.UserDto;
import com.xy.domain.entity.Role;
import com.xy.domain.entity.User;
import com.xy.domain.entity.UserRole;
import com.xy.domain.vo.PageVo;
import com.xy.domain.vo.UserInfoVo;
import com.xy.domain.vo.UserVo;
import com.xy.domain.vo.UserVo1;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.mapper.UserMapper;
import com.xy.service.RoleService;
import com.xy.service.UserRoleService;
import com.xy.service.UserService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author Xy
 * @since 2023-05-28 20:47:37
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;

    /**
     * 查询用户信息
     * @return
     */
    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        UserInfoVo vo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        //返回
        return ResponseResult.okResult(vo);
    }

    /**
     * 修改sys_user中的信息
     * @param user
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        //对数据进行是否存在的判断
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        if(emailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        //对密码进行加密处理
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //之后再存到数据库当中
        save(user);
        return ResponseResult.okResult();
    }

    /**
     * 后台获取用户列表
     * @param pageNum
     * @param pageSize
     * @param userDto
     * @return
     */
    @Override
    public ResponseResult getUserList(Integer pageNum, Integer pageSize, UserDto userDto) {
        //获取所有用户 或者 通过用户名、手机号、状态来查询
        LambdaQueryWrapper<User> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(userDto.getUserName()),User::getUserName,userDto.getUserName());
        queryWrapper.like(StringUtils.hasText(userDto.getPhonenumber()),User::getPhonenumber,userDto.getPhonenumber());
        queryWrapper.eq(StringUtils.hasText(userDto.getStatus()),User::getStatus,userDto.getStatus());
        //分页
        Page<User> page=new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        //封装数据并返回
        List<User> users=page.getRecords();
        List<UserVo> userVos=BeanCopyUtils.copyBeanList(users,UserVo.class);
        PageVo pageVo=new PageVo(userVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 后台新增用户时确保用户名唯一
     * @param user
     * @return
     */
    @Override
    public boolean checkUserNameUnique(User user) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,user.getUserName());
        return count(queryWrapper)==0;
    }

    /**
     * 后台新增用户时确保手机号唯一
     * @param user
     * @return
     */
    @Override
    public boolean checkPhoneUnique(User user) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhonenumber,user.getPhonenumber());
        return count(queryWrapper)==0;
    }

    /**
     * 后台新增用户时确保邮箱唯一
     * @param user
     * @return
     */
    @Override
    public boolean checkEmailUnique(User user) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,user.getEmail());
        return count(queryWrapper)==0;
    }

    /**
     * 后台新增用户
     * @param user
     * @return
     */
    @Override
    public ResponseResult addUser(User user) {
        //密码加密处理
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
        if(user.getRoleIds()!=null&&user.getRoleIds().length>0){
            userRoleService.insertUserRole(user);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteUser(String id) {
        String[] temps = id.split(",");
        List<Long> userIds =new ArrayList<>();
        for (String temp:temps){
            Long userId=Long.valueOf(temp);
            if (getById(userId).getDelFlag().equals(Integer.valueOf(SystemConstants.HAS_DELETED))) {
                throw new SystemException(AppHttpCodeEnum.HAS_DELETED);
            }
            else if(!getById(userId).getDelFlag().equals(Integer.valueOf(SystemConstants.HAS_NOT_DELETED))){
                throw new SystemException(AppHttpCodeEnum.HAS_NOT_DELETED);
            }
            userIds.add(userId);
        }
        //删除user_role表的数据
        userRoleService.deleteUserRoleByUserId(id);
        //去执行更新del_Flag操作
        getBaseMapper().updatedelFlagById(userIds);
        return ResponseResult.okResult();
    }

    /**
     * 得到用户的详细信息
     * @param id
     * @return
     */
    @Override
    public ResponseResult getUserDetails(Long id) {
        //当前用户所具有的角色id列表
        List<Role> roles = roleService.selectRoleAll();
        List<Long> roleIds = roleService.selectRoleIdByUserId(id);
        //获取roleIds
        //获取roles
        //获取user
        //封装并返回
        UserVo1 userVo1=new UserVo1(roleIds,roles,getById(id));
        return ResponseResult.okResult(userVo1);
    }

    /**
     * 更新用户
     * @param user
     */
    @Override
    @Transactional
    public void updateUser(User user) {
        // 更新用户信息
        updateById(user);
        // 更改user_role表 并且先删后加
        // 删除用户与角色关联
        LambdaQueryWrapper<UserRole> userRoleUpdateWrapper = new LambdaQueryWrapper<>();
        userRoleUpdateWrapper.eq(UserRole::getUserId,user.getId());
        userRoleService.remove(userRoleUpdateWrapper);
        // 新增用户与角色管理
        insertUserRole(user);
    }

    /**
     * 新增用户与角色管理
     * @param user
     */
    private void insertUserRole(User user) {
        List<UserRole> sysUserRoles = Arrays.stream(user.getRoleIds())
                .map(roleId -> new UserRole(user.getId(), roleId)).collect(Collectors.toList());
        userRoleService.saveBatch(sysUserRoles);
    }


    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        return count(queryWrapper)>0;
    }
    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,nickName);
        return count(queryWrapper)>0;
    }
    private boolean emailExist(String email) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail,email);
        return count(queryWrapper)>0;
    }



}

