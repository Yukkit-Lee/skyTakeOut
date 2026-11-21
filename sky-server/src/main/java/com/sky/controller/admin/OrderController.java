package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: 44219
 * @since: 2026/11/16 10:50
 * @description:none
 */
@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api("订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("查询所有订单")
    // 此处如果加RequestParam Sping会从query参数找一个叫ordersPageQueryDTO的参数并转换为DTO对象
    public Result<PageResult> getAllOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult ordersByPage = orderService.getAdminOrdersByPage(ordersPageQueryDTO);
        return Result.success(ordersByPage);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result getOrderDetail(@PathVariable("id") Long orderId){
        OrderVO orderDetails = orderService.getOrderDetails(orderId);
        return Result.success(orderDetails);
    }

    @GetMapping("/statistics")
    @ApiOperation("各个订单状态统计")
    public Result getStatistics(){
        OrderStatisticsVO statistics = orderService.getStatistics();
        return Result.success(statistics);
    }

}
