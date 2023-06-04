package com.xy.domain.dto;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO 通常用于展示层(Controller)和服务层(Service)之间的数据传输对象
 * VO 通常是impl实现类 利用其封装结果 传到前端去
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagListDto {

    private String name;
    //备注
    private String remark;

}
