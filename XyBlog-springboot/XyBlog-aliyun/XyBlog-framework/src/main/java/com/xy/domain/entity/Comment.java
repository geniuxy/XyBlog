package com.xy.domain.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 评论表(Comment)表实体类
 *
 * @author Xy
 * @since 2023-05-29 16:23:29
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("xy_comment")
@ApiModel(description = "评论entity")
//实际业务中不会直接在Comment上写@ApiModel 可能会导致维护起来太麻烦了
//会创建DTO类用于swagger 便于维护
//在接口中使用时 利用beanCopyUtils转化成实体类
public class Comment  {
    @TableId
    @ApiModelProperty(notes = "主键Id")
    private Long id;

    //评论类型（0代表文章评论，1代表友链评论）
    @ApiModelProperty(notes = "评论类型（0代表文章评论，1代表友链评论）")
    private String type;
    //文章id
    @ApiModelProperty(notes = "文章Id")
    private Long articleId;
    //根评论id
    @ApiModelProperty(notes = "根评论Id")
    private Long rootId;
    //评论内容
    private String content;
    //所回复的目标评论的userid
    private Long toCommentUserId;
    //回复目标评论id
    private Long toCommentId;
    /**
     * 创建人的用户id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新人的用户id
     */
    //填充和更新的时候的会自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;



}
