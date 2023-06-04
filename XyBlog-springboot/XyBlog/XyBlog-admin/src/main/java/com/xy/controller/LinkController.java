package com.xy.controller;

import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Link;
import com.xy.domain.vo.PageVo;
import com.xy.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {
    @Autowired
    private LinkService linkService;

    /**
     * 获取所有友链或者符合条件的友链
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getLinkList(Integer pageNum, Integer pageSize, Link link){
        PageVo pageVo=linkService.getLinkList(pageNum,pageSize,link);
        return ResponseResult.okResult(pageVo);
    }
    /**
     * 新增友链
     */
    @PostMapping
    public ResponseResult add(@RequestBody Link link){
        linkService.save(link);
        return ResponseResult.okResult();
    }

    /**
     * 根据id查询友联
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public ResponseResult getInfo(@PathVariable(value = "id")Long id){
        Link link = linkService.getById(id);
        return ResponseResult.okResult(link);
    }
    /**
     * 更改友链
     */
    @PutMapping
    public ResponseResult edit(@RequestBody Link link){
        linkService.updateById(link);
        return ResponseResult.okResult();
    }

    /**
     * 删除友链
     */
    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id){
        linkService.removeById(id);
        return ResponseResult.okResult();
    }

    /**
     * 改变友链状态(前端没做这个功能)
     * @param link
     * @return
     */
    @PutMapping("/changeLinkStatus")
    public ResponseResult changeLinkStatus(@RequestBody Link link){
        linkService.updateById(link);
        return ResponseResult.okResult();
    }

}
