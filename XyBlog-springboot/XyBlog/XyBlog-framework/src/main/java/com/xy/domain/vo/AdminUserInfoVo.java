package com.xy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserInfoVo {
    //包括三部分：
    //用户信息+权限信息+角色信息
    private List<String> permissions;

    private List<String> roles;

    private UserInfoVo user;

}
