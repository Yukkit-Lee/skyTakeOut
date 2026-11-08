package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/6 10:23
 * @description:none
 */
@Mapper
public interface ShoppingCartMapper {

    /** 插入购物车记录 */
    //@Insert("INSERT INTO shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) VALUES (#{shoppingCart.name},#{shoppingCart.image},#{shoppingCart.user_id},#{shoppingCart.dish_id},#{shoppingCart.setmeal_id},#{shoppingCart.dish_flavor},#{shoppingCart.amount},#{shoppingCart.create_time})")
    int insertIntoCart(ShoppingCart shoppingCart);

    /** 查询当前 菜品或套餐 是否已存在 */
    ShoppingCart searchExist(ShoppingCart shoppingCart);

    /** 更新购物车信息 */
    int modifyCart(ShoppingCart shoppingCart);

    /** 查看购物车信息 */
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId}")
    List<ShoppingCart> getShoppingCart(Long userId);

    /** 清空购物车 */
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    int cleanShoppingCart(Long userId);
}
