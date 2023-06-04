package com.xy.controller;

import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Comment;
import com.xy.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Api(description = "评论接口")
//@Api(tags="评论",description = "评论相关接口")//在Controller层可以加上@Api接口来实现改变标题和描述
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/commentList")
    @ApiOperation(value="文章评论列表",notes="获取一页评论列表")
    public ResponseResult commentList(Long articleId,Integer pageNum,Integer pageSize){
        return commentService.commentList(SystemConstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }

    @PostMapping
    @ApiOperation(value="添加评论",notes="增加一条评论")
    public ResponseResult addcomment(@RequestBody Comment comment){
        return commentService.addcomment(comment);
    }

    @GetMapping("/linkCommentList")
    @ApiOperation(value="友链评论列表",notes="获取一页友链评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum",value = "页号"),
            @ApiImplicitParam(name = "pageSize",value = "每页大小")
    })
    public ResponseResult linkCommentList(Integer pageNum,Integer pageSize){
        //希望也能调用commentList方法来查询友链评论
        //comment中type属性 0表示正常评论 1表示友链评论
        return commentService.commentList(SystemConstants.LINK_COMMENT,null,pageNum,pageSize);
    }

}
