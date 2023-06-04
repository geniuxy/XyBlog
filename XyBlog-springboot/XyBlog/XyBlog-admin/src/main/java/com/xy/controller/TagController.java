package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.dto.TagListDto;
import com.xy.domain.entity.Tag;
import com.xy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    /**
     * 查询标签列表
     * @return
     */
    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        return tagService.pageTagList(pageNum,pageSize,tagListDto);
    }

    /**
     * 新增标签
     */
    @PostMapping
    public ResponseResult addTag(@RequestBody Tag tag){
        return tagService.addTag(tag);
    }

    /**
     * 删除标签以及批量删除标签
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteTagById(@PathVariable String id){
        //这是我自己研究实现的第一个功能
        //以后不知道前端传回来的是什么格式
        //可以先试试前端会报什么错
        //一般出错信息里就可以知道了
        return tagService.deleteTagById(id);
    }

    /**
     * 获取标签
     */
    @GetMapping("/{id}")
    public ResponseResult getTag(@PathVariable Long id){
        return tagService.getTag(id);
    }
    /**
     * 更新标签
     */
    @PutMapping
    public ResponseResult updateTag(@RequestBody Tag tag){
        //更新操作用entity类就行了
        return tagService.updateTag(tag);
    }
    /**
     *
     */
    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        return tagService.listAllTag();
    }
}
