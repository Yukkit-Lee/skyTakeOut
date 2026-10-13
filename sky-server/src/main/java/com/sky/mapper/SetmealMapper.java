package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author: 44219
 * @since: 2026/10/13 10:12
 * @description:none
 */

@Mapper
public interface SetmealMapper {

    @Select("SELECT COUNT(*) FROM setmeal WHERE category_id=#{categoryId}")
    int countByCategoryId(long categoryId);
}
