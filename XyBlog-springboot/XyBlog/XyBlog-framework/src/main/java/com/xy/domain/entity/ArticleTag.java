package com.xy.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 文章标签关联表(ArticleTag)表实体类
 *
 * @author Xy
 * @since 2023-06-01 18:07:14
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("xy_article_tag")
public class ArticleTag  implements Serializable{
    //serialVersionUID常量来简化序列化数据的版本控制
    //TODO 玛德 不会讲人话吗 这个serialVersionUID干嘛的啊
    private static final long serialVersionUID = 625337492348897098L;
    //文章id
    private Long articleId;
    //标签id
    private Long tagId;
}
