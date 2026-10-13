package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/12 10:19
 * @description:none
 */
public interface CategoryService {

    /** 新增分类数据 */
    int insertNewCategory(CategoryDTO categoryDTO);

    /** 修改分类数据 */
    int  modifyCategory(CategoryDTO categoryDTO);

    /** 分页查询分类数据 */
    PageResult getCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    /** 修改分类状态 */
    int modifyStatus(Integer status, long id);

    /** 根据id删除分类 */
    int deleteCategory(long id);

    /** 根据type查询 */
    List<Category> getCategoryByType(Integer type);
}
