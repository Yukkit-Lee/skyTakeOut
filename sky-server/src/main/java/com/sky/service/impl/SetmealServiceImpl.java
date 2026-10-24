package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Authorization;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/24 10:04
 * @description:none
 */
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    @Transactional
    /** 新增套餐 */
    public void insertSetmeal(SetmealDTO setmealDTO) {
        // 要对两个表进行操作 -> setmeal, setmeal_dish

        // TODO: mapper层调用切面方法设置setmeal对象创建时间创建人等信息
        Setmeal curSetMeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, curSetMeal);
        int affectRows = setmealMapper.insertSetmeal(curSetMeal);
        if (affectRows == 0)
            throw new IllegalStateException("插入setmeal表失败");


        // setmeal_dish表中需要回显setmeal_id,dish_id(前端传过来的)
        Long curSetmealId = curSetMeal.getId();   //当前套餐id这一个事务中唯一
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();    //获取setmealDish列表

        if(setmealDishList==null || setmealDishList.isEmpty())
            throw new IllegalStateException("存在未传入空对象");

        //遍历设置其套餐id 插入操作动态sql完成 不调用多次数据库
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(curSetmealId);
        }
        int smdAffectRows = setMealDishMapper.insertSetMealDish(setmealDishList);
        if (smdAffectRows != setmealDishList.size())
            throw new IllegalStateException("插入setmealDish表失败");

        return ;
    }

    @Override
    public PageResult getSetmealInfo(SetmealPageQueryDTO setmealPageQueryDTO) {

        // PageHelper开启分页
        if(setmealPageQueryDTO.getPageSize()==0)
            setmealPageQueryDTO.setPageSize(10);
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> setmealInfoByPage = setmealMapper.getSetmealInfoByPage(setmealPageQueryDTO);
        PageResult pageResult=new PageResult(setmealInfoByPage.getTotal(),setmealInfoByPage.getResult());

        return pageResult;
    }

}
