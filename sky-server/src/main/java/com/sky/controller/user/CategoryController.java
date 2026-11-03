package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/3 10:36
 * @description:none
 */
@RestController("userCategoryController")   //BeanDefinitionStoreException
@RequestMapping("/user/category")
@Api(value = "C端分类接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/list")
    @ApiOperation("查询所有分类")
    public Result getCategory(@RequestParam(required = false) Integer type){
        List<Category> categoryByType = categoryService.getCategoryByType(type);
        return Result.success(categoryByType);
    }
}
