package com.xy.domain.vo;

import com.xy.domain.entity.Role;
import com.xy.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo1 {
    private List<Long> roleIds;
    private List<Role> roles;
    private User user;
}
