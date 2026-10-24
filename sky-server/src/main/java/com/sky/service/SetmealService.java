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
}
