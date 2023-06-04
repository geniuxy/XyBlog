package com.xy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xy.domain.entity.Tag;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;


/**
 * 标签(Tag)表数据库访问层
 *
 * @author Xy
 * @since 2023-05-31 13:03:09
 */
public interface TagMapper extends BaseMapper<Tag> {

    void updatedelFlagById(List<Long> tagId);
}
