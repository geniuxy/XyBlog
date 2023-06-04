package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Article;
import com.xy.domain.entity.Category;
import com.xy.domain.vo.CategoryVo;
import com.xy.domain.vo.PageVo;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.mapper.CategoryMapper;
import com.xy.service.ArticleService;
import com.xy.service.CategoryService;
import com.xy.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author Xy
 * @since 2023-05-28 17:48:02
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ArticleService articleService;

    /**
     * 前台查询所有分类
     * @return
     */

    @Override
    public ResponseResult getCategoryList() {
        //查找文章表xy_article 找到其中状态为已发表的文章
        LambdaQueryWrapper<Article> articleWrapper= new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> articleList=articleService.list(articleWrapper);
        //获取文章的分类ID，并且去重
        Set<Long> categoryIds = articleList.stream()
                .map(article -> article.getCategoryId())
                .collect(Collectors.toSet());
        //查询分类表
        List<Category> categories = listByIds(categoryIds);
            //这边默认就是用的CategoryServiceImpl的CategoryService的listByIds()方法 查的就是对应的xy_category
        categories =categories.stream().filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
        //封装vo
        List<CategoryVo> CategoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);

        return ResponseResult.okResult(CategoryVos);
    }

    /**
     * 后台查询所有分类
     * 和前台的区别就是：前台是以发表文章的分类list，而后台是所有分类的分类list
     * @return
     */
    @Override
    public ResponseResult listAllCategory() {
        //查询Category表 找出其中状态正常的Category
        LambdaQueryWrapper<Category> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus,SystemConstants.NORMAL);
        //生成个表
        List<Category> list =list(queryWrapper);
        //封装成Vo表 并返回
        List<CategoryVo> categoryVoList = BeanCopyUtils.copyBeanList(list, CategoryVo.class);
        return ResponseResult.okResult(categoryVoList);
    }

    /**
     * 查询分类
     * @param pageNum
     * @param pageSize
     * @param category
     * @return
     */
    @Override
    public PageVo selectCategoryPage(Integer pageNum, Integer pageSize, Category category) {
        //先查询所有分类 或者符合条件的分类
        LambdaQueryWrapper<Category> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(category.getName()),Category::getName,category.getName());
        queryWrapper.eq(StringUtils.hasText(category.getStatus()),Category::getStatus,category.getStatus());
        //分页
        Page<Category> page=new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        List<Category> categories=page.getRecords();
        PageVo pageVo =new PageVo(categories,page.getTotal());
        return pageVo;
    }

    @Override
    public void deleteCategoryById(String id) {
        String[] temps = id.split(",");
        List<Long> categoryIds =new ArrayList<>();
        for (String temp:temps){
            Long categoryId=Long.valueOf(temp);
            if (getById(categoryId).getDelFlag().equals(Integer.valueOf(SystemConstants.HAS_DELETED))) {
                throw new SystemException(AppHttpCodeEnum.HAS_DELETED);
            }
            else if(!getById(categoryId).getDelFlag().equals(Integer.valueOf(SystemConstants.HAS_NOT_DELETED))){
                throw new SystemException(AppHttpCodeEnum.HAS_NOT_DELETED);
            }
            categoryIds.add(categoryId);
        }
        //去执行更新del_Flag操作
        getBaseMapper().updatedelFlagById(categoryIds);
    }

}

