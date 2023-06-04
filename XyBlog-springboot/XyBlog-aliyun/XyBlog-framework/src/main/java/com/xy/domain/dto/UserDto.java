package com.xy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String userName;

    //手机号
    private String phonenumber;


    //账号状态（0正常 1停用）
    private String status;


}
