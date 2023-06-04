package com.xy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListDto {
    //标题
    private String title;
    //文章摘要
    private String summary;

}
