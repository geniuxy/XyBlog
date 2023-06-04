package com.xy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Link;
import com.xy.domain.vo.PageVo;


/**
 * 友链(Link)表服务接口
 *
 * @author Xy
 * @since 2023-05-28 20:22:13
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    PageVo getLinkList(Integer pageNum, Integer pageSize, Link link);
}

