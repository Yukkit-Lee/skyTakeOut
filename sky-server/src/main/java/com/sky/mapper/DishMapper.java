package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/13 10:12
 * @description:none
 */

@Mapper
public interface DishMapper {

    @Select("SELECT COUNT(*) FROM dish WHERE category_id=#{categoryId}")
    int countByCategoryId(long categoryId);

    @AutoFill(value = OperationType.INSERT)
    int insertNewDish(Dish dish);

    Page<Dish> getDish(DishPageQueryDTO dishPageQueryDTO);

    Dish getDishByDishId(Integer id);

    /** 根据categoryId查询菜品 */
    @Select("SELECT * FROM dish where category_id=#{categoryId}")
    List<Dish> getDishByCateId(Integer categoryId);
}
