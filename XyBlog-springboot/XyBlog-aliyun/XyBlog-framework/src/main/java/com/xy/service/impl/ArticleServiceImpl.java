package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.dto.AddArticleDto;
import com.xy.domain.dto.ArticleDto;
import com.xy.domain.dto.ArticleListDto;
import com.xy.domain.entity.Article;
import com.xy.domain.entity.ArticleTag;
import com.xy.domain.entity.Category;
import com.xy.domain.entity.Tag;
import com.xy.domain.vo.*;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.mapper.ArticleMapper;
import com.xy.service.ArticleService;
import com.xy.service.ArticleTagService;
import com.xy.service.CategoryService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.RedisCache;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleTagService articleTagService;

    /**
     * 查询热门文章 并在首页展示部分信息
     * @return
     */
    @Override
    public ResponseResult hotArticleList() {
        //查询热门文章，以ResponseResult格式返回
        //queryWrapper是mybatisplus中实现查询的对象封装操作类
        LambdaQueryWrapper<Article> queryWrapper =new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);//Article::getStatus可以防止字符串报错
        //按照浏览量进行排序
        queryWrapper.orderByDesc(Article::getViewCount);
        // 最多只能查出来10条消息 Page()
        Page<Article> page =new Page<>(1,10);
        page(page,queryWrapper);
        List<Article> articles=page.getRecords();
//        //bean拷贝
//        List<HotArticleVo> articleVos=new ArrayList<>();
//        for(Article article : articles){
//            HotArticleVo vo =new HotArticleVo();
//            //bean拷贝 把article里的数据传到vo中
//            BeanUtils.copyProperties(article,vo);
//            articleVos.add(vo);
//        }
        for(Article article :articles){
            //从redis中获取viewCount
            //即实现 实时更新访问量 的功能
            Integer viewCount = redisCache.getCacheMapValue(SystemConstants.KEY_ARTICLE_VIEWCOUNT, article.getId().toString());
            article.setViewCount(viewCount.longValue());
        }
        //用自定义bean拷贝工具实现bean拷贝
        List<HotArticleVo> articleVos = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);

        return ResponseResult.okResult(articleVos);
    }

    /**
     * 获得文章列表
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId) {
        //查询条件 首页：查询所有的文章 分类页面：查询对应分类下的文章
        LambdaQueryWrapper<Article> queryWrapper =new LambdaQueryWrapper<>();
        //如果有categoryId就要查询时要和传入的相同
        //即可做到    首页：查询所有的文章 分类页面：查询对应分类下的文章
        queryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0,Article::getCategoryId,categoryId);
        //状态是已发布的
        queryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        //对is_Top进行降序(1的会在上方)
        queryWrapper.orderByDesc(Article::getIsTop);
        //分页查询
        //MybatisPlus 使用分页功能的时候 要配置分页拦截器
        Page<Article> page=new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        //查询categoryName
        List<Article> articles=page.getRecords();
        //普通循环方式实现获取categoryName
        for(Article article :articles){
            Category category = categoryService.getById(article.getCategoryId());
            article.setCategoryName(category.getName());
            //从redis中获取viewCount
            //即实现 实时更新访问量 的功能
            Integer viewCount = redisCache.getCacheMapValue(SystemConstants.KEY_ARTICLE_VIEWCOUNT, article.getId().toString());
            article.setViewCount(viewCount.longValue());
        }
        //stream流方法实现获取categoryName
//        articles.stream().map(new Function<Article, Article>() {
//            @Override
//            public Article apply(Article article) {
//                article.setCategoryName(categoryService.getById(article.getCategoryId()).getName());
//                return article;
//            }
//        });
        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);
        //传入PageVo中 再传入ResponseResult
        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 获得文章详情
     * @param id
     * @return
     */
    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article =getById(id);
        //从redis中获取viewCount
        //即实现 实时更新访问量 的功能
        Integer viewCount = redisCache.getCacheMapValue(SystemConstants.KEY_ARTICLE_VIEWCOUNT, id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成VO
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //根据分类id查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        //封装响应返回
        return ResponseResult.okResult(articleDetailVo);
    }

    /**
     * 自增文章访问量
     * @param id
     * @return
     */
    @Override
    public ResponseResult updateViewCount(Long id) {
        redisCache.incrementCacheMapValue(SystemConstants.KEY_ARTICLE_VIEWCOUNT,id.toString(),1);
        return ResponseResult.okResult();
    }

    /**
     * 后台添加博客
     * @param articleDto
     * @return
     */
    @Override
    @Transactional//声明事务
    //方法中两件事情:保存博客内容+保存博客和标签关系
    //必须保证事务
    public ResponseResult add(AddArticleDto articleDto) {
        //添加博客
        Article article =BeanCopyUtils.copyBean(articleDto,Article.class);
        save(article);
        //添加article和tag的关系
        List<ArticleTag> articleTags =articleDto.getTags().stream()
                .map(tagId->new ArticleTag(article.getId(),tagId))
                .collect(Collectors.toList());
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult list(Integer pageNum, Integer pageSize, ArticleListDto articleDto) {
        //分页查询
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.eq()三个参数的时候 第一个参数为true 才会执行后面的判断
        //queryWrapper.like()进行数据的模糊查询
        queryWrapper.like(StringUtils.hasText(articleDto.getTitle()), Article::getTitle, articleDto.getTitle());
        queryWrapper.like(StringUtils.hasText(articleDto.getSummary()), Article::getSummary, articleDto.getSummary());
        //进行分页
        Page<Article> page = new Page<>();//新建page对象
        page.setCurrent(pageNum);//当前页
        page.setSize(pageSize);//页大小
        page(page, queryWrapper);
        //封装数据并返回
        List<Article> articles = page.getRecords();
        List<ArticleVo> articleVos=BeanCopyUtils.copyBeanList(articles,ArticleVo.class);
        PageVo pageVo = new PageVo(articleVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 后台得到博文详情
     * @param id
     * @return
     */
    @Override
    public ResponseResult ArticleDetails(Long id) {
        //获取tagIds
        List<Long> tagIds=getBaseMapper().getTagsByArticleIDs(id);
        //封装VO
        ArticleUpdateVo articleUpdateVo=BeanCopyUtils.copyBean(getById(id),ArticleUpdateVo.class);
        articleUpdateVo.setTags(tagIds);
        return ResponseResult.okResult(articleUpdateVo);
    }

    /**
     * 更新article和article_tag表
     * @param articleDto
     * @return
     */
    @Override
    public ResponseResult updateArticle(ArticleDto articleDto) {
        //更新DTO中除了tags之外的部分
        Article article =BeanCopyUtils.copyBean(articleDto,Article.class);
        updateById(article);
        //更改tags也就是更改xy_article_tag表
        //先删除原有article和tag关联数据
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId,article.getId());
        articleTagService.remove(queryWrapper);
        //再添加新的article和tag的关联数据
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(articleDto.getId(), tagId))
                .collect(Collectors.toList());
        //articleTagService自带好多方法
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteArticleById(String id) {
        String[] temps = id.split(",");
        List<Long> articleIds =new ArrayList<>();
        for (String temp:temps){
            Long articleId=Long.valueOf(temp);
            if (getById(articleId).getDelFlag().equals(Integer.valueOf(SystemConstants.HAS_DELETED))) {
                throw new SystemException(AppHttpCodeEnum.HAS_DELETED);
            }
            else if(!getById(articleId).getDelFlag().equals(Integer.valueOf(SystemConstants.HAS_NOT_DELETED))){
                throw new SystemException(AppHttpCodeEnum.HAS_NOT_DELETED);
            }
            articleIds.add(articleId);
        }
        //去执行更新del_Flag操作
        getBaseMapper().updatedelFlagById(articleIds);
        return ResponseResult.okResult();
    }

    @Override
    public void updateViewCountById(Long id, Long viewCount) {
        getBaseMapper().updateViewCountById(id,viewCount);
    }
}
