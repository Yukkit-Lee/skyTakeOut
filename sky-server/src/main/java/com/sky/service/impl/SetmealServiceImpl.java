package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Authorization;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
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

        if (setmealDishList == null || setmealDishList.isEmpty())
            throw new IllegalStateException("存在未传入空对象");

        //遍历设置其套餐id 插入操作动态sql完成 不调用多次数据库
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(curSetmealId);
        }
        int smdAffectRows = setMealDishMapper.insertSetMealDish(setmealDishList);
        if (smdAffectRows != setmealDishList.size())
            throw new IllegalStateException("插入setmealDish表失败");

        return;
    }

    @Override
    public PageResult getSetmealInfo(SetmealPageQueryDTO setmealPageQueryDTO) {

        // PageHelper开启分页
        if (setmealPageQueryDTO.getPageSize() == 0)
            setmealPageQueryDTO.setPageSize(10);
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> setmealInfoByPage = setmealMapper.getSetmealInfoByPage(setmealPageQueryDTO);
        PageResult pageResult = new PageResult(setmealInfoByPage.getTotal(), setmealInfoByPage.getResult());

        return pageResult;
    }

    @Override
    public SetmealVO searchSetmealById(Long setmealId) {
        //后端再次验证是否空传
        if (setmealId == null) return null;


        SetmealVO setmealVO = setmealMapper.searchSetmealById(setmealId);
        if (setmealVO == null) return null;    //数据库中没有相关套餐直接返回null

        //setmealDishes为null 调用setMealDishMapper
        List<SetmealDish> seatMealDishById =
                setMealDishMapper.getSeatMealDishBySetmealId(setmealId);
        setmealVO.setSetmealDishes(seatMealDishById);

        return setmealVO;
    }

    @Override
    public void changeStatus(Integer status, Long id) {

        Setmeal newSetmeal = new Setmeal();
        newSetmeal.setStatus(status);
        newSetmeal.setId(id);
        int changeMsgCode = setmealMapper.modifySetmeal(newSetmeal);
        if (changeMsgCode == 0) throw new IllegalStateException("修改状态失败");

        return;
    }

    @Override
    @Transactional
    public void modifySetMeal(SetmealDTO setmealDTO) {
        // 更新setmeal -> 更新setmealDish(根据setmeal_id匹配 先删再插)

        Setmeal curSetMeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, curSetMeal);
        int setmealUpdateCode = setmealMapper.modifySetmeal(curSetMeal);
        if (setmealUpdateCode == 0) throw new IllegalStateException("修改setmeal失败");

        Long curSetmealId = setmealDTO.getId();
        int deleteRows = setMealDishMapper.deleteInfoBySetmealId(curSetmealId);

        //为SetmealDish List中每一个对象setmealId赋值
        List<SetmealDish> curSetmealDish = setmealDTO.getSetmealDishes();
        if (curSetmealDish == null || curSetmealDish.isEmpty())
            throw new IllegalStateException("List<SetmealDish>为空");
        for (SetmealDish setmealDish : curSetmealDish) {
            setmealDish.setSetmealId(curSetmealId);
        }

        int insertRows = setMealDishMapper.insertSetMealDish(curSetmealDish);
        if (insertRows != curSetmealDish.size()) throw new IllegalStateException("修改setmeal失败");


        return;
    }

    @Override
    @Transactional
    public void deleteSetmeal(List<Integer> ids) {

        // 批量删除套餐信息 : 处于起售状态的商品套餐不能删除
        // 还需要同步删除setmeal_dish表中信息
        if (ids == null || ids.isEmpty())
            throw new DeletionNotAllowedException("idList为空");

        // ids转为LinkedHashSet进行去重
        // 1.元素不可重复 2.保留加入顺序
        LinkedHashSet<Integer> linkedHashIdSet=new LinkedHashSet<>(ids);
        List<Integer> uniqueIdArr=new ArrayList<>(linkedHashIdSet);

        for (Integer id : uniqueIdArr) {
            Setmeal setmealInfo = setmealMapper.getSetmealInfo(id);
            if(setmealInfo==null)
                throw new DeletionNotAllowedException("存在未知id");
            if(setmealInfo.getStatus()== StatusConstant.ENABLE)
                throw new DeletionNotAllowedException("存在套餐在售,不可删除");
        }

        int affectRows = setmealMapper.deleteSetmeal(uniqueIdArr);
        if (affectRows != uniqueIdArr.size())
            throw new DeletionNotAllowedException("批量删除setmeal表失败");

        setMealDishMapper.deleteSetmealByIds(uniqueIdArr);



    }

}
