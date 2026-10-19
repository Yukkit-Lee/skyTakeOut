package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/14 11:09
 * @description:新增菜品接口开发
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result insertDish(@RequestBody DishDTO dishDTO) {
        dishService.insertDish(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询")
    public Result<PageResult> getDishByPage(DishPageQueryDTO dishPageQueryDTO){
        return Result.success(dishService.getDishByPage(dishPageQueryDTO));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品")
    public Result getDishById(@PathVariable Integer id){
        return Result.success(dishService.getDishById(id));
    }

    @GetMapping("/list")
    @ApiOperation(value = "根据分类id查询菜品")
    /** 在后端建立 URL 参数名 categoryId → Java 变量名 */
    public Result getDishByCateId(@RequestParam("categoryId") Integer cateId){
        List<Dish> dishList = dishService.getDishByCateId(cateId);
        return dishList==null?Result.success("查询结果为空"):Result.success(dishList);
    }

}
