package com.xy.service;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.User;

public interface AdminLoginService {


    ResponseResult login(User user);

    ResponseResult logout();
}
