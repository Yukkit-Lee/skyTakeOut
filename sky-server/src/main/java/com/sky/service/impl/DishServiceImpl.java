package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private SetMealDishMapper setMealDishMapper;

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


        Dish dish = dishMapper.getDishByDishId(Long.valueOf(id));

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

        if (cateId == null)
            throw new RuntimeException("参数为空");

        List<Dish> dishList = dishMapper.getDishByCateId(cateId);

        if (dishList.isEmpty()) return null;
        return dishList;
    }

    @Override
    public PageResult getDishByPageVo(DishPageQueryDTO dishPageQueryDTO) {
        if (dishPageQueryDTO.getPageSize() == 0) dishPageQueryDTO.setPageSize(10);

        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> dishPageVo = dishMapper.getDishPageVo(dishPageQueryDTO);
        PageResult pageResult = new PageResult(dishPageVo.getTotal(), dishPageVo.getResult());

        return pageResult;
    }

    @Override
    @Transactional
    public int deleteDish(List<Long> idArr) {

        if (idArr.isEmpty())
            throw new IllegalStateException("传入参数为空!");
        // 对请求发来的ids进行数组构造 dishIdString: ids=46,47,1
        // List<Integer> idArr = new ArrayList<>();
        // String[] arrStrArr = dishIdString.split(",");
        // for (String s : arrStrArr) {
        //     Integer curInteger = Integer.valueOf(s);
        //     idArr.add(curInteger);
        // }
        /**
         * 1.判断是否存在 在售菜品？
         * 2.判断是否被套餐关联？
         * 3.删除菜品关联口味信息
         * */
        // 1.判断是否存在 在售菜品？
        for (Long curId : idArr) {
            Dish curDish = dishMapper.getDishByDishId(curId);
            if (curDish.getStatus() == StatusConstant.ENABLE)
                throw new DeletionNotAllowedException("菜品在售中禁止删除");

        }

        // 2.判断是否被套餐关联？
        if (!setMealDishMapper.getSeatMealDishById(idArr).isEmpty())    //列表没查到返回[] != null
            throw new DeletionNotAllowedException("菜品已和套餐相关联");

        // 3.删除口味信息
        for (Long curId : idArr) {
            int deleteMsgCode = dishFlavorMapper.deleteFlavor(curId);
            if (deleteMsgCode == 0)
                System.out.println("存在dish_id不匹配的口味信息");
        }

        //DELETE FROM dish WHERE id in (trueId,noExistId) -> 执行成功 只删除trueId数据
        int msgCode = dishMapper.deleteDish(idArr);

        return msgCode;
    }

    /**
     * 可能会插入flavors表 需开启事务
     */
    @Override
    @Transactional
    public void modifyDish(DishDTO dishDTO) {

        // 修改基本菜品信息
        Dish newDish = new Dish();
        BeanUtils.copyProperties(dishDTO, newDish);
        int affectRows = dishMapper.modifyDish(newDish);
        if (affectRows == 0) throw new IllegalStateException("修改菜品信息失败!");


        List<DishFlavor> curFlavors = dishDTO.getFlavors();

        // TODO:此处补全flavor mapper
        //  flavors 非必须 如果非空则需要修改flavors表
        if (curFlavors != null) {   // 若curFlavors为null直接调用isEmpty会抛异常
            // 先把关联此菜品的flavor全部删除
            dishFlavorMapper.deleteFlavor(dishDTO.getId()); //若为null也不影响

            // 再根据用户新添加的口味数据插入
            if (!curFlavors.isEmpty()) {
                for (DishFlavor newFlavor : curFlavors) {
                    // 前端只传过来name和value 并没有dishId
                    newFlavor.setDishId(dishDTO.getId());
                }
                int insertRows = dishFlavorMapper.insertFlavorList(curFlavors);
                if (insertRows != curFlavors.size())
                    throw new IllegalStateException("菜品口味信息插入失败");
            }
        }

    }

    @Override
    public int changeStauts(Long dishId, Integer status) {
        Dish curDish = new Dish();
        curDish.setStatus(status);
        curDish.setId(dishId);

        return dishMapper.modifyDish(curDish);
    }
}
