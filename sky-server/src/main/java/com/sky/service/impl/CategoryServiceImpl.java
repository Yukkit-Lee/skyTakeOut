package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/12 10:20
 * @description:none
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealMapper setmealMapper;

    @Override
    public int insertNewCategory(CategoryDTO categoryDTO) {

        Category newCategory = new Category();
        BeanUtils.copyProperties(categoryDTO, newCategory);  //拷贝DTO对象原有属性

        newCategory.setStatus(StatusConstant.DISABLE);   //默认禁用
//        newCategory.setCreateTime(LocalDateTime.now());
//        newCategory.setUpdateTime(LocalDateTime.now());
//
//        newCategory.setCreateUser(BaseContext.getCurrentId());
//        newCategory.setUpdateUser(BaseContext.getCurrentId());

        return categoryMapper.insertNewCategory(newCategory);
    }

    @Override
    public int modifyCategory(CategoryDTO categoryDTO) {

        Category newCategory = new Category();
        BeanUtils.copyProperties(categoryDTO, newCategory);

        newCategory.setUpdateUser(BaseContext.getCurrentId());
        newCategory.setUpdateTime(LocalDateTime.now());

        return categoryMapper.modifyCategory(newCategory);
    }

    @Override
    public PageResult getCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        if (categoryPageQueryDTO.getPageSize() == 0)
            categoryPageQueryDTO.setPageSize(10);

        /** Mapper层省去写limit关键字 */
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        Page<Category> categoryInfo = categoryMapper.getCategoryInfo(categoryPageQueryDTO);

        PageResult pageResult = new PageResult(categoryInfo.getTotal(), categoryInfo.getResult());
        return pageResult;
    }

    @Override
    public int modifyStatus(Integer status, long id) {
        /** 此处仅需要创建一个需要修改属性的对象传给mapper */
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
//                .updateTime(LocalDateTime.now())
//                .updateUser(BaseContext.getCurrentId())
//                .build();

        return categoryMapper.modifyCategory(category);
    }

    @Override
    public int deleteCategory(long id) {

        if (dishMapper.countByCategoryId(id) > 0)
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        if (setmealMapper.countByCategoryId(id) > 0)
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);

        return categoryMapper.deleteCategory(id);
    }

    @Override
    public List<Category> getCategoryByType(Integer type) {
        return categoryMapper.getCategoryInfoByType(type);
    }
}
