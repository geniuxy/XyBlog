package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Category;
import com.xy.domain.vo.CategoryVo;
import com.xy.domain.vo.PageVo;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author Xy
 * @since 2023-05-28 17:14:55
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    ResponseResult listAllCategory();

    PageVo selectCategoryPage(Integer pageNum, Integer pageSize, Category category);

    void deleteCategoryById(String id);
}

