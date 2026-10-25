package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/20 10:52
 * @description:none
 */
@Mapper
public interface SetMealDishMapper {

    /** 根据菜品id查询套餐信息 **/
    // SELECT * FROM seatmeal_dish WHERE dish_id in (x,x,x,xx,x,x,);
    List<SetmealDish> getSeatMealDishById(List<Long> idArr);

    /** 插入 */
    int insertSetMealDish(List<SetmealDish> setmealDishes);

    /** 根据套餐id查询套餐信息 **/
    List<SetmealDish> getSeatMealDishBySetmealId(Long id);

    /** 根据setmeal id删除 */
    int deleteInfoBySetmealId(Long setmealId);
}
