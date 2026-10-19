package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/15 10:11
 * @description:none
 */
public interface DishService {

    /** 插入新菜品 */
    void insertDish(DishDTO dishDTO);

    /** 分页查询菜品 */
    PageResult getDishByPage(DishPageQueryDTO dishPageQueryDTO);

    /** 根据id查询菜品
     * @return DishVO*/
    DishVO getDishById(Integer id);

    /** 根据分类Id查询菜品 */
    List<Dish> getDishByCateId(Integer cateId);
}
