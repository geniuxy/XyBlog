package com.xy.job;

import com.xy.constants.SystemConstants;
import com.xy.domain.entity.Article;
import com.xy.service.ArticleService;
import com.xy.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;


    //指定定时任务的时间：从0s开始，每隔5s执行一次
    //秒（0~59），分钟（0~59），小时（0~23），日期（1-月最后一天），月份（1-12），星期几（1-7,1表示星期日）
    // *星号表示任意值
    // ,可以用来定义列表 比如：1,2,3
    // -用来表示范围 比如：1-3
    //日期部分还可允许特殊字符： ? L W(倒数第三个)
    //星期部分还可允许的特殊字符: ? L #(倒数第一个)
    // ？用来防止冲突 日期和星期 同时使用?和同时不使用?都是不对的  比如： 2 * 2和 ？ * ？因为2号不一定是星期二
    // W(用于日期）表示当月中最接近某天的工作日 比如：31W表示每个月的最后一天
    // L(用于日期和星期) 表示每月最后一天   也可以表示每月倒是第N天。例如： L-2表示每个月的倒数第2天
    // L W可以连起来用，LW表示每月最后一个工作日，即每月最后一个星期五
    // L 用于星期 表示 月最后一个星期6  而 1L~6L 分别表示 月最后一个星期日、星期一、...、星期六
    // #(用于星期) 表示 月第几个星期几 比如：6#3表示 月中第3个星期五
    @Scheduled(cron = "* 0/10 * * * ?")
    public void updateViewCount() {
        //获取redis中的浏览量
        Map<String, Integer> viewCountMap = redisCache.getCacheMap(SystemConstants.KEY_ARTICLE_VIEWCOUNT);
        List<Article> articles = viewCountMap.entrySet()
                .stream()
                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue()))
                .collect(Collectors.toList());
        //更新到数据库中
        //articleService.updateBatchById(articles);
        articles.forEach(article -> {
            Long viewCount = article.getViewCount();
            Long viewCount1 = articleService.getById(article.getId()).getViewCount();
            if (viewCount != viewCount1) {
                articleService.updateViewCountById(article.getId(), viewCount);
            }
        });
    }


}
