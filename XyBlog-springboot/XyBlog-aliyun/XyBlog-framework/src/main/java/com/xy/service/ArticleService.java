package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.dto.AddArticleDto;
import com.xy.domain.dto.ArticleDto;
import com.xy.domain.dto.ArticleListDto;
import com.xy.domain.entity.Article;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto articleDto);

    ResponseResult list(Integer pageNum, Integer pageSize, ArticleListDto articleDto);

    ResponseResult ArticleDetails(Long id);

    ResponseResult updateArticle(ArticleDto articleDto);

    ResponseResult deleteArticleById(String id);
}
