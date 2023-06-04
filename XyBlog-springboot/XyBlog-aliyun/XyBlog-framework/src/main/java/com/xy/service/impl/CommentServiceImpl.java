package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Comment;
import com.xy.domain.vo.CommentVo;
import com.xy.domain.vo.PageVo;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.mapper.CommentMapper;
import com.xy.service.CommentService;
import com.xy.service.UserService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author Xy
 * @since 2023-05-29 16:23:30
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        //查询对应文章的根评论

        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        //对articleId进行判断
        //只有在type=0即文章评论是，articleId才会有
        //queryWrapper.eq()可以加一个变量 来判断 ，只有第一个变量为true时，才会执行后面的判断
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);
        //根评论的rootId=-1
        queryWrapper.eq(Comment::getRootId, SystemConstants.ROOT_ID_NORMAL);
        //判断评论类型是文章评论还是友链评论
        queryWrapper.eq(Comment::getType,commentType);
        //分页查询
        Page<Comment> page =new Page<>(pageNum,pageSize);
        page(page,queryWrapper);

        //最后响应体里 data里有row和total
        //其中toCommentUserName和username在Comment里没有，所以需要额外创建
        List<CommentVo> commentVoList=toCommentVoList(page.getRecords());
        //查询所有根评论对应的子评论集合children，并且赋值给CommentVo
        for(CommentVo commentVo:commentVoList){
            //查询对应的子评论
            List<CommentVo> children = getChildren(commentVo.getId());
            //赋值
            commentVo.setChildren(children);
        }
        return ResponseResult.okResult(new PageVo(commentVoList,page.getTotal()));
    }

    /**
     * 方法：添加评论
     * @return
     */
    @Override
    public ResponseResult addcomment(Comment comment) {
        //如果用户目前没有登录就无法评论
        Authentication authentication = SecurityUtils.getAuthentication();
        //判断是否认证通过
        if(authentication.getPrincipal().equals("anonymousUser")){
            throw new SystemException(AppHttpCodeEnum.NOLOGIN_NOCOMMENT);
        }
        // 评论内容不能为空
        if(!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        //从SecurityContextHolder中获得userId
        //从而来获得createby creattime updateby updatetime
        //securityUtils工具类是用来从SecurityContextHolder中获得userId，减少代码耦合
        //comment.setCreateBy(SecurityUtils.getUserId());
        //上行这个方法在securityUtils方法中完成了
        //save是IService的一个方法
        save(comment);//在执行save前需要给几个响应体中不存在的属性赋值，即完成自动填充
        return ResponseResult.okResult();
    }

    /**
     * 根据根评论的id查询所对应的子评论的集合
     * @param id
     * @return
     */
    private List<CommentVo> getChildren(Long id) {
        //这边子评论的个数可能比较少，所以就不分页了
        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        //遍历所有评论的rootid
        queryWrapper.eq(Comment::getRootId,id);
        //根据创建时间CreateTime排序
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> comments=list(queryWrapper);
        List<CommentVo> commentVos=toCommentVoList(comments);
        return commentVos;
    }

    /**
     * 方法：将List<Comment>转换为List<CommentVo>格式
     * @param list
     * @return
     */
    //page.getRecords()是List<Comment>类型
    //将List<Comment>转换为List<CommentVo>格式
    private List<CommentVo> toCommentVoList(List<Comment> list){
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历Vo集合
        for(CommentVo commentVo:commentVos){
            //通过creatyBy查询userID并赋值
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            //通过toCommentUserId查询toCommentUserNamev
            //如果toCommentUserId不为默认值-1，才进行查询
            if(commentVo.getToCommentUserId()!=-1){
                String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getNickName();
                commentVo.setToCommentUserName(toCommentUserName);
            }
        }
        return commentVos;
    }
}

