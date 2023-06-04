package com.xy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.domain.entity.Article;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {
    List<Long> getTagsByArticleIDs(Long articleId);

    void updatedelFlagById(List<Long> articleIds);

    void updateViewCountById(@Param("id")Long id, @Param("view_count") Long viewCount);
}
