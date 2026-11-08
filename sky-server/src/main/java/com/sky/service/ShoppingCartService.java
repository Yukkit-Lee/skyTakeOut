package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/6 10:21
 * @description:none
 */
public interface ShoppingCartService {

    /** 购物车添加 */
    int addToCart(ShoppingCartDTO shoppingCartDTO);

    /** 查看购物车 */
    List<ShoppingCart> getShoppingCart();

    /** 清空购物车 */
    int cleanShoppingCart();
}
