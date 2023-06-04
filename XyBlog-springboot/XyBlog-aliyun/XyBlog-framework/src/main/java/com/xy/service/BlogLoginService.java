package com.xy.service;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult loginout();
}
