package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/15 10:11
 * @description:none
 */
@Component
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    @Transactional  //开启事务 (事务抛异常才会终止)
    public void insertDish(DishDTO dishDTO) {

        // 1.向菜品表插入数据
        Dish newDish = new Dish();
        BeanUtils.copyProperties(dishDTO, newDish);
        if (newDish.getStatus() == null) newDish.setStatus(StatusConstant.DISABLE);

        int dishMsgCode = dishMapper.insertNewDish(newDish);

        // 中间应当获取菜品id传给口味表(主键回显)
        Long curDishId = newDish.getId();

        /** 检查菜品插入是否成功 */
        if (dishMsgCode != 1 || curDishId == null)
            throw new IllegalStateException("新增菜品失败");

        // 2.向口味表插入数据  (flavors不为空的情况下)
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            // 动态SQL中用foreach来写 下方循环遍历为flavor添加菜品id
            for (DishFlavor curFlavor : flavors) {
                curFlavor.setDishId(curDishId);
            }

            int falvorRows = dishFlavorMapper.insertFlavorList(dishDTO.getFlavors());
            if (falvorRows != flavors.size())
                throw new IllegalStateException("新增菜品口味失败");
        }

    }


    // TODO: 接口文档中 categoryName 字段缺失
    @Override
    public PageResult getDishByPage(DishPageQueryDTO dishPageQueryDTO) {
        // 首先处理异常逻辑
        if (dishPageQueryDTO.getPageSize() == 0) dishPageQueryDTO.setPageSize(10);

        // 开启分页
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<Dish> dishInfo = dishMapper.getDish(dishPageQueryDTO);

        PageResult pageResult = new PageResult(dishInfo.getTotal(), dishInfo.getResult());
        return pageResult;

    }

    @Override
    public DishVO getDishById(Integer id) {


        Dish dish = dishMapper.getDishByDishId(id);

        if (dish != null) {
            //缺失数据: 分类相关属性(categoryId存在 需要补全categoryName)、口味相关属性
            Category categoryInfo = categoryMapper.getCategoryInfoByDishId(dish.getCategoryId());

            List<DishFlavor> dishFlavor = dishFlavorMapper.getDishFlavorByDishId(id);

            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setCategoryName(categoryInfo.getName());
            dishVO.setFlavors(dishFlavor);

            return dishVO;
        }
        return null;
    }

    @Override
    public List<Dish> getDishByCateId(Integer cateId) {

        if(cateId==null)
            throw new RuntimeException("参数为空");

        List<Dish> dishList = dishMapper.getDishByCateId(cateId);

        if (dishList.isEmpty()) return null;
        return dishList;
    }
}
