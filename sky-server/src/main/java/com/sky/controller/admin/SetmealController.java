package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: 44219
 * @since: 2026/10/24 09:58
 * @description:none
 */
@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api(value = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation(value = "新增套餐")
    public Result insertSeatmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.insertSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询")
    /**  此处前端请求参数为普通query类型 拿RequestParam接受 此处只是后端把其封装为dto对象
     *   故不需要@RequestBody 否则报错Required request body is missing
     * */
    public Result<PageResult> getSetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult setmealInfo = setmealService.getSetmealInfo(setmealPageQueryDTO);
        return Result.success(setmealInfo);
    }

    @PutMapping
    @ApiOperation(value = "修改套餐")
    public Result modifySeatmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.modifySetMeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询套餐")
    public Result searchSeatmeal(@PathVariable("id") Long setmealId){
        SetmealVO setmealVO = setmealService.searchSetmealById(setmealId);
        return Result.success(setmealVO);
    }

    @PostMapping("/status/{status}")
    @ApiOperation(value = "套餐状态变更")
    public Result changeState(@PathVariable Integer status,@RequestParam Long id){
        setmealService.changeStatus(status,id);
        return Result.success();
    }
}
