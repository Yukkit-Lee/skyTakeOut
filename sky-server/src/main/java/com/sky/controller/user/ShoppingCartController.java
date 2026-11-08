package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/6 10:17
 * @description:none
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Api("购物车相关接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        int msgCode = shoppingCartService.addToCart(shoppingCartDTO);
        return msgCode==0?Result.error("加购失败"):Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result showShoppingCart(){
        List<ShoppingCart> shoppingCarts = shoppingCartService.getShoppingCart();
        return Result.success(shoppingCarts);
    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result cleanShoppingCart(){
        int affectRows = shoppingCartService.cleanShoppingCart();
        return Result.success(affectRows);
    }
}
