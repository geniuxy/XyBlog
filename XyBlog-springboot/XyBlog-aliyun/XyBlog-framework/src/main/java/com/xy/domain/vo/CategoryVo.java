package com.xy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVo {
    private Long id;
    //分类名
    private String name;
    //描述
    //前台貌似不需要描述
    //后台需要
    private String description;
}
