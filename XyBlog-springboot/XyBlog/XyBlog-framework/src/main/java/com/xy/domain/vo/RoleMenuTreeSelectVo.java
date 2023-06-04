package com.xy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RoleMenuTreeSelectVo {
    //menuIds
    private List<Long> checkedKeys;

    private List<MenuTreeVo> menus;

}
