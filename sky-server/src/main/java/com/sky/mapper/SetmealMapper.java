package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author: 44219
 * @since: 2026/10/13 10:12
 * @description:none
 */

@Mapper
public interface SetmealMapper {

    /** 根据分类id查询套餐数量 */
    @Select("SELECT COUNT(*) FROM setmeal WHERE category_id=#{categoryId}")
    int countByCategoryId(long categoryId);

    /** 新增套餐 */
    @AutoFill(value = OperationType.INSERT)
    int insertSetmeal(Setmeal setmeal);

    /** 分页查询套餐 */
    Page<SetmealVO> getSetmealInfoByPage(SetmealPageQueryDTO setmealPageQueryDTO);
}
