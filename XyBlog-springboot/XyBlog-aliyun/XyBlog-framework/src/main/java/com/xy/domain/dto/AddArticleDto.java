package com.xy.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddArticleDto {
    //标题
    private String title;
    //缩略图
    private String thumbnail;
    //是否置顶（0否，1是）
    private String isTop;
    //是否允许评论 1是，0否
    private String isComment;
    //文章内容
    private String content;
    //便签集合(Article entity里原本没有的)
    private List<Long> tags;
    //所属分类id
    private Long categoryId;
    //文章摘要
    private String summary;
    //状态（0已发布，1草稿）
    private String status;
}
