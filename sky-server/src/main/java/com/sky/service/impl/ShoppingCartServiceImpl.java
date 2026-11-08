package com.sky.service.impl;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/6 10:22
 * @description:none
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional
    public int addToCart(ShoppingCartDTO shoppingCartDTO) {
        // 根据前端传过来的值构造shoppingcart对象
        ShoppingCart curCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, curCart);
        curCart.setUserId(BaseContext.getCurrentId());
        curCart.setCreateTime(LocalDateTime.now());

        // 先判断当前用户购物车中是否存在相同菜品套餐
        ShoppingCart searchedCart = shoppingCartMapper.searchExist(curCart);
        if (searchedCart != null) {
            Integer curNumber = searchedCart.getNumber();
            searchedCart.setNumber(curNumber + 1);
            int updateMsgCode = shoppingCartMapper.modifyCart(searchedCart);
            if (updateMsgCode != 1) throw new IllegalStateException("购物车数量添加失败");
            return 1;
        }

        // 单次添加只可能为 套餐/菜品 2选1
        if (curCart.getDishId() != null) {
            // ..
            Dish curDish = dishMapper.getDishByDishId(curCart.getDishId());
            curCart.setName(curDish.getName());
            curCart.setImage(curDish.getImage());
            curCart.setAmount(curDish.getPrice());
        } else if (curCart.getSetmealId() != null) {
            Setmeal curSetmeal = setmealMapper.getSetmealInfo(Math.toIntExact(curCart.getSetmealId()));
            curCart.setName(curSetmeal.getName());
            curCart.setImage(curSetmeal.getImage());
            curCart.setAmount(curSetmeal.getPrice());
        }

        // amount为商品单价 冗余字段
        curCart.setNumber(1);
        int msgCode = shoppingCartMapper.insertIntoCart(curCart);

        return msgCode;
    }

    @Override
    public List<ShoppingCart> getShoppingCart() {
        Long curUserId = BaseContext.getCurrentId();
        return shoppingCartMapper.getShoppingCart(curUserId);
    }

    @Override
    @Transactional
    public int cleanShoppingCart() {
        Long curUserId = BaseContext.getCurrentId();
        int affectRows = shoppingCartMapper.cleanShoppingCart(curUserId);
        if (affectRows == 0) throw new IllegalStateException("清空购物车失败");

        return affectRows;

    }
}
