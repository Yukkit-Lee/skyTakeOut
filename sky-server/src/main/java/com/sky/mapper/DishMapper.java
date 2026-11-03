package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
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

    Dish getDishByDishId(Long id);

    /** 根据categoryId查询菜品 */
    @Select("SELECT * FROM dish where category_id=#{categoryId}")
    List<Dish> getDishByCateId(Integer categoryId);

    /**
     * 根据菜品对象中非空的条件查询菜品。
     * 用户端按分类查询时，传入 categoryId 和 status，只返回该分类下对应状态的菜品。
     *
     * @param dish 查询条件；当前使用 categoryId 和 status
     * @return 符合条件的菜品列表，未查到时返回空列表
     */
    List<Dish> list(Dish dish);

    /** 外连接分页查询菜品*/
    Page<DishVO> getDishPageVo(DishPageQueryDTO dishPageQueryDTO);

    /** 批量删除dish信息 */
    int deleteDish(List<Long> idArr);

    /** 修改菜品信息 */
    int modifyDish(Dish dish);
}
