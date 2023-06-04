package com.xy.utils;


import com.xy.domain.entity.Menu;
import com.xy.domain.vo.MenuTreeVo;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 转换器！？
 */
public class SystemConverter {

    private SystemConverter() {
    }

    /**
     * 建立菜单树
     * @param menus
     * @return
     */
    public static List<MenuTreeVo> buildMenuSelectTree(List<Menu> menus) {
        //从menus列表转化为MenuTreeVo
        List<MenuTreeVo> MenuTreeVos = menus.stream()
                .map(m -> new MenuTreeVo(m.getId(), m.getMenuName(), m.getParentId(), null))
                .collect(Collectors.toList());
        //从ParentId=0的menu开始setChildren
        //这一步是得到所有ParentId=0的menu
        List<MenuTreeVo> options = MenuTreeVos.stream()
                .filter(o -> o.getParentId().equals(0L))
                .map(o -> o.setChildren(getChildList(MenuTreeVos, o)))
                .collect(Collectors.toList());
        return options;
    }


    /**
     * 得到子节点列表
     */
    private static List<MenuTreeVo> getChildList(List<MenuTreeVo> list, MenuTreeVo option) {
        //对每个MenuTreeVo进行操作得到子节点列表
        List<MenuTreeVo> options = list.stream()
                .filter(o -> Objects.equals(o.getParentId(), option.getId()))
                //再得到子节点的子节点
                .map(o -> o.setChildren(getChildList(list, o)))
                .collect(Collectors.toList());
        return options;

    }
}
