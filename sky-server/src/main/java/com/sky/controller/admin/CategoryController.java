package com.sky.controller.admin;
/**
 * @author: 44219
 * @since: 2026/10/12 10:16
 * @description:员工管理
 */

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @ApiOperation(value = "新增分类")
    public Result insertCategory(@RequestBody CategoryDTO categoryDTO) {
        int msgCode = categoryService.insertNewCategory(categoryDTO);
        return msgCode == 1 ? Result.success() : Result.error("unknown error");
    }

    @GetMapping("/page")
    @ApiOperation(value = "分类分页查询")
    public Result<PageResult> showCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult category = categoryService.getCategory(categoryPageQueryDTO);
        return Result.success(category);
    }

    @PutMapping
    @ApiOperation(value = "修改分类信息")
    public Result modifyCategory(@RequestBody CategoryDTO categoryDTO) {
        int msgCode = categoryService.modifyCategory(categoryDTO);
        return Result.success(msgCode);
    }

    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用禁用分类")
    public Result modifyStatusCategory(@PathVariable Integer status,long id) {
        int msgCode = categoryService.modifyStatus(status,id);
        return Result.success(msgCode);
    }

    @DeleteMapping
    @ApiOperation(value = "根据id删除分类")
    public Result deleteCategory(@RequestParam long id) {
        int msgCode = categoryService.deleteCategory(id);
        return Result.success(msgCode);
    }

    @GetMapping("/list")
    @ApiOperation(value = "根据类型查询分类")
    public Result<List<Category>> getCategoryByType(@RequestParam(required = false) Integer type) {
        List<Category> categoryByType = categoryService.getCategoryByType(type);
        return Result.success(categoryByType);
    }
}
    // localhost:8080/admmin/category/list?type=2