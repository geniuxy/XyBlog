package com.xy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.constants.SystemConstants;
import com.xy.domain.ResponseResult;
import com.xy.domain.dto.TagListDto;
import com.xy.domain.entity.Tag;
import com.xy.domain.vo.PageVo;
import com.xy.domain.vo.TagVo;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.exception.SystemException;
import com.xy.mapper.TagMapper;
import com.xy.service.TagService;
import com.xy.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author Xy
 * @since 2023-05-31 13:03:10
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        //分页查询
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.eq()三个参数的时候 第一个参数为true 才会执行后面的判断
        queryWrapper.eq(StringUtils.hasText(tagListDto.getName()), Tag::getName, tagListDto.getName());
        queryWrapper.eq(StringUtils.hasText(tagListDto.getRemark()), Tag::getRemark, tagListDto.getRemark());
        //分页
        Page<Tag> page = new Page<>();//新建page对象
        page.setCurrent(pageNum);//当前页
        page.setSize(pageSize);//页大小
        page(page, queryWrapper);
        //封装数据并返回
        List<Tag> tags = page.getRecords();
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(tags, TagVo.class);
        PageVo pageVo = new PageVo(tagVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 添加标签
     * @param tag
     * @return
     */
    @Override
    public ResponseResult addTag(Tag tag) {
        if (!StringUtils.hasText(tag.getName())) {
            throw new SystemException(AppHttpCodeEnum.TAG_NAME_NOT_NULL);
        }
        save(tag);
        return ResponseResult.okResult();
    }

    /**
     * 批量删除标签
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteTagById(String id) {
        String[] temps = id.split(",");
        List<Long> tagIds =new ArrayList<>();
        for (String temp:temps){
            Long tagId=Long.valueOf(temp);
            //如果此Tag已删除
            if (getById(tagId).getDelFlag().equals(Integer.valueOf(SystemConstants.HAS_DELETED))) {
                throw new SystemException(AppHttpCodeEnum.HAS_DELETED);
            }//如果是别的状态码
            else if(!getById(tagId).getDelFlag().equals(Integer.valueOf(SystemConstants.HAS_NOT_DELETED))){
                throw new SystemException(AppHttpCodeEnum.HAS_NOT_DELETED);
            }
            //遍历结果传入tagIds中
            tagIds.add(tagId);
        }
        //剩下的就是此Tag未删除的情况
        //去执行更新del_Flag操作
        getBaseMapper().updatedelFlagById(tagIds);
        return ResponseResult.okResult();
    }

    /**
     * 获取标签信息
     * @param id
     * @return
     */
    @Override
    public ResponseResult getTag(Long id) {
        Tag tag = getById(id);
        TagVo tagVo =BeanCopyUtils.copyBean(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    /**
     * 更新标签信息
     * @param tag
     * @return
     */
    @Override
    public ResponseResult updateTag(Tag tag) {
        updateById(tag);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listAllTag() {
        //查询所有标签
        LambdaQueryWrapper<Tag> queryWrapper =new LambdaQueryWrapper<>();
        //通过select方法对所有数据进行去重？罗列？获取？
        queryWrapper.select(Tag::getId,Tag::getName);//Id不能一样Name也不能一样
        List<Tag> tags = list(queryWrapper);
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(tags, TagVo.class);
        //TODO 这边Vo命名是三个属性 remake为null 为什么响应体里没有remake
        return ResponseResult.okResult(tagVos);
    }
}

