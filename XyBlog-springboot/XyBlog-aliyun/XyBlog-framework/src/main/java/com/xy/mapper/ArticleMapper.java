package com.xy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.domain.entity.Article;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {
    List<Long> getTagsByArticleIDs(Long articleId);

    void updatedelFlagById(List<Long> articleIds);
}
