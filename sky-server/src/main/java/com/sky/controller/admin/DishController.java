package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/14 11:09
 * @description:菜品接口开发
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品接口")
@Slf4j
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
    public Result<PageResult> getDishByPage(DishPageQueryDTO dishPageQueryDTO) {
        return Result.success(dishService.getDishByPageVo(dishPageQueryDTO));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品")
    public Result getDishById(@PathVariable Integer id) {
        return Result.success(dishService.getDishById(id));
    }

    @GetMapping("/list")
    @ApiOperation(value = "根据分类id查询菜品")
    /** 在后端建立 URL 参数名 categoryId → Java 变量名 */
    public Result getDishByCateId(@RequestParam("categoryId") Integer cateId) {
        List<Dish> dishList = dishService.getDishByCateId(cateId);
        return dishList == null ? Result.success("查询结果为空") : Result.success(dishList);
    }

    @DeleteMapping
    @ApiOperation(value = "批量删除菜品")
    /** 此处加RequestParam注解 mvc框架直接将其解析为数组 */
    public Result deleteDish(@RequestParam("ids") List<Long> idArr) {
//        int affectRows = dishService.deleteDish(deleteStr);
//        return Result.success(affectRows);
        log.info("菜品idArr:{}",idArr);
        int affectRows = dishService.deleteDish(idArr);
        return Result.success(affectRows);
    }

    @PutMapping
    @ApiOperation(value = "修改菜品信息")
    public Result modifyDishInfo(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息");
        dishService.modifyDish(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation(value = "菜品停售起售")
    public Result changeDishStatus(@RequestParam("id")Long DishId,@PathVariable Integer status) {
        int affectRows=dishService.changeStauts(DishId,status);

        return Result.success(affectRows);
    }

}
