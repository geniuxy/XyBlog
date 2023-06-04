package com.xy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.domain.entity.Category;

import java.util.List;


/**
 * 分类表(Category)表数据库访问层
 *
 * @author Xy
 * @since 2023-05-28 17:14:54
 */
public interface CategoryMapper extends BaseMapper<Category> {

    void updatedelFlagById(List<Long> categoryIds);
}
