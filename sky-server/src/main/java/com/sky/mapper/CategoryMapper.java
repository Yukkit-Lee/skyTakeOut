package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/12 10:20
 * @description:none
 */
@Mapper
public interface CategoryMapper {

    int insertNewCategory(Category category);

    Page<Category> getCategoryInfo(CategoryPageQueryDTO categoryPageQueryDTO);

    int modifyCategory(Category newCategory);

    int deleteCategory(long id);

    List<Category> getCategoryInfoByType(Integer type);
}
