package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.dto.AddArticleDto;
import com.xy.domain.dto.ArticleDto;
import com.xy.domain.dto.ArticleListDto;
import com.xy.domain.dto.TagListDto;
import com.xy.domain.vo.ArticleVo;
import com.xy.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    /**
     * 新建添加博文
     * @return
     */
    //与标签的关联关系保存 文章的内容保存
    //以上这两件事应该保证是事务
    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto articleDto){
        return articleService.add(articleDto);
    }


    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, ArticleListDto articleDto){
        return articleService.list(pageNum,pageSize,articleDto);
    }

    /**
     * 查询文章详情接口
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult ArticleDetails(@PathVariable Long id){
        return articleService.ArticleDetails(id);
    }
    /**
     * 更新文章接口
     */
    @PutMapping
    public ResponseResult updateArticle(@RequestBody ArticleDto articleDto){
        return articleService.updateArticle(articleDto);
    }

    /**
     * 删除博文以及批量删除博文
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteArticleById(@PathVariable String id){
        return articleService.deleteArticleById(id);
    }
}
