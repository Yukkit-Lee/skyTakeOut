package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/15 10:44
 * @description:none
 */
@Mapper
public interface DishFlavorMapper {

     @Insert("INSERT INTO dish_flavor (dish_id, name, value) VALUES " +
             "(#{dishId},#{name},#{value})")
     int insertFlavor(DishFlavor dishFlavor);

     /** 插入多条flavor数据 */
     int insertFlavorList(List<DishFlavor> dishFlavorList);

     /** 根据菜品id查询口味 */
     @Select("SELECT * FROM dish_flavor WHERE dish_id = #{dishId}")
     List<DishFlavor> getDishFlavorByDishId(Integer dishId);

     /**  修改flavor信息 */
     int modifyFlavor(DishFlavor dishFlavor);

     /** 根据菜品id删除口味信息 */
     @Delete("DELETE FROM dish_flavor WHERE dish_id=#{dishId}")
     int deleteFlavor(Long dishId);
}
