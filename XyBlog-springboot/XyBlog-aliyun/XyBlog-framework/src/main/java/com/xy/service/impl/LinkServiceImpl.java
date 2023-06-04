package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Link;
import com.xy.domain.vo.LinkVo;
import com.xy.domain.vo.PageVo;
import com.xy.mapper.LinkMapper;
import com.xy.service.LinkService;
import com.xy.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author Xy
 * @since 2023-05-28 20:22:13
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        //查询所有审核通过的友链
        LambdaQueryWrapper<Link> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links=list(queryWrapper);
        //转换为VO
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(links, LinkVo.class);
        //封装并返回
        return ResponseResult.okResult(linkVos);
    }
    @Override
    public PageVo getLinkList(Integer pageNum, Integer pageSize, Link link) {
        LambdaQueryWrapper<Link> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(link.getName()),Link::getName,link.getName());
        queryWrapper.eq(StringUtils.hasText(link.getStatus()),Link::getStatus,link.getStatus());
        Page<Link> page=new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        List<Link> links=page.getRecords();
        PageVo pageVo =new PageVo(links,page.getTotal());
        return pageVo;
    }
}

