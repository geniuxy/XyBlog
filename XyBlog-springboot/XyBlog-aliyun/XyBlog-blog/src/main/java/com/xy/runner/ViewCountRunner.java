package com.xy.runner;

import com.xy.constants.SystemConstants;
import com.xy.domain.entity.Article;
import com.xy.mapper.ArticleMapper;
import com.xy.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 程序启动时，执行任务配置：访问文章自动将访问量存到redis中
 * 访问量接口流程  第一步： 在开机时将数据库中每篇文章的viewCount存到redis中
 */
@Component
@Slf4j
public class ViewCountRunner implements CommandLineRunner {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void run(String... args) throws Exception {
        //查询博客文章信息 id viewCount
        List<Article> articles=articleMapper.selectList(null);
//        articles.stream()
//                .collect(Collectors.toMap(new Function<Article, Long>() {
//                    @Override//用来获取id
//                    public Long apply(Article article) {
//                        return article.getId();
//                    }
//                }, new Function<Article, Integer>() {
//                    @Override//用来获取viewCount
//                    public Integer apply(Article article) {
//                        //为什么不存Long类型的数据
//                        //Long类型数据存到redis中是无法自增的
//                        return article.getViewCount().intValue();
//                    }
//                }));
        //用来获取id
        //用来获取viewCount
        Map<String, Integer> viewCountMap = articles.stream()
                .collect(Collectors.toMap(article1 -> article1.getId().toString(), article -> {
                    return article.getViewCount().intValue();//Long类型数据存到redis中是无法自增的
                }));
        //存储到redis中
        //这里key是个常量 是个字符串 相当于一个标题
        //而value值 是个Map Map里存着文章id(key)和viewCount(view)
        redisCache.setCacheMap(SystemConstants.KEY_ARTICLE_VIEWCOUNT,viewCountMap);

        log.info("-----------------------已完成启动任务-----------------------");
    }
}
