package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Comment;


/**
 * 评论表(Comment)表服务接口
 *
 * @author Xy
 * @since 2023-05-29 16:23:30
 */
public interface CommentService extends IService<Comment> {

    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    ResponseResult addcomment(Comment comment);
}

