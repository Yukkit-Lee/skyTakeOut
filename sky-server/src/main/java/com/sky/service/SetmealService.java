package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

/**
 * @author: 44219
 * @since: 2026/10/24 10:03
 * @description:none
 */
public interface SetmealService {

    /** 新增套餐 */
    public void insertSetmeal(SetmealDTO setmealDTO);

    /** 套餐分页查询 */
    PageResult getSetmealInfo(SetmealPageQueryDTO setmealPageQueryDTO);

    /** 根据套餐id查询 */
    SetmealVO searchSetmealById(Long setmealId);

    /** 更改套餐状态 */
    void changeStatus(Integer status,Long id);

    /** 更新套餐信息 */
    void modifySetMeal(SetmealDTO setmealDTO);
}
