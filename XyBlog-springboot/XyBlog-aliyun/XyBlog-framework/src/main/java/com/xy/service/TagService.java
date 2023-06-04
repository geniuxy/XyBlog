package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.dto.TagListDto;
import com.xy.domain.entity.Tag;


/**
 * 标签(Tag)表服务接口
 *
 * @author Xy
 * @since 2023-05-31 13:03:10
 */
public interface TagService extends IService<Tag> {

    ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult addTag(Tag tag);

    ResponseResult deleteTagById(String id);

    ResponseResult getTag(Long id);

    ResponseResult updateTag(Tag tag);

    ResponseResult listAllTag();
}

