package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Article;
import com.xy.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList(){
        ResponseResult result =articleService.hotArticleList();
        return result;
    }

    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){//命名一样可以省略
        return articleService.getArticleDetail(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable Long id){//从url中可以获取的数据 用@PathVariable
        return articleService.updateViewCount(id);
    }

//    @GetMapping("/searchList")
//    public ResponseResult searchList(Integer pageNum,Integer pageSize,String keyword){
//        return articleService.searchList(pageNum,pageSize,keyword);
//    }

}
