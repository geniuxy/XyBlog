package com.xy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.domain.entity.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 用户表(User)表数据库访问层
 *
 * @author Xy
 * @since 2023-05-28 20:47:36
 */
public interface UserMapper extends BaseMapper<User> {

    void updatedelFlagById(List<Long> userIds);
}
