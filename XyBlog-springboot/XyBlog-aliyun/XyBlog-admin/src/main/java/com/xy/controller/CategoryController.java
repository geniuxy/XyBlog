package com.xy.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.xy.domain.ResponseResult;
import com.xy.domain.entity.Category;
import com.xy.domain.vo.CategoryVo;
import com.xy.domain.vo.ExcelCategoryVo;
import com.xy.domain.vo.PageVo;
import com.xy.enums.AppHttpCodeEnum;
import com.xy.service.CategoryService;
import com.xy.utils.BeanCopyUtils;
import com.xy.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/content/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 查询所有分类接口
     * 用于写博客页面点击去就可以罗列所有分类
     */
    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory(){
//        List<CategoryVo> list = categoryService.listAllCategory();
//        return ResponseResult.okResult(list);
        return categoryService.listAllCategory();
    }
    /**
     * 导出分类至excel
     * 利用EasyExcel实现
     * 除此之外还需要对导出接口进行权限设置
     * 就比如有的用户没有分类管理这个权限
     * 但是别有用心的人可能会直接利用token来获取，不通过按钮
     * 所以要控制权限
     */
    @PreAuthorize("@ps.hasPermission('content:category:export')")
    //在执行接口前会先执行任务，来判断是否有权限
    @GetMapping("/export")
    public void export(HttpServletResponse response){
        try {
            //设置下载文件的请求头
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            //获取需要导出的分类数据
            List<Category> categoryVos = categoryService.list();
            //BEAN转换
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categoryVos, ExcelCategoryVo.class);
            //把数据写入到Excel中
            //这里需要设置不关闭流
            //sheet工作薄的名称(就是excel下面的那个)
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE)
                    .sheet("分类导出").doWrite(excelCategoryVos);
        } catch (Exception e) {
            //如果出现异常也要响应json
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    /**
     * 后台得到分类列表
     * @param pageNum
     * @param pageSize
     * @param category
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getCategoryList(Integer pageNum,Integer pageSize,Category category){
        PageVo pageVo = categoryService.selectCategoryPage(pageNum,pageSize,category);
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        categoryService.save(category);
        return ResponseResult.okResult();
    }
    /**
     * 根据id查询分类
     */
    @GetMapping("/{id}")
    public ResponseResult getCategoryDetails(@PathVariable Long id){
        Category category = categoryService.getById(id);
        return ResponseResult.okResult(category);
    }
    /**
     * 修改分类
     */
    @PutMapping
    public ResponseResult updateCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return ResponseResult.okResult();
    }
    /**
     * 删除分类以及批量删除分类
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteCategoryById(@PathVariable String id){
        categoryService.deleteCategoryById(id);
        return ResponseResult.okResult();
    }


}
