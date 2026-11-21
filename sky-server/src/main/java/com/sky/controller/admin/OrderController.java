package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
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
    public Result<PageResult> getAllOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult ordersByPage = orderService.getAdminOrdersByPage(ordersPageQueryDTO);
        return Result.success(ordersByPage);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result getOrderDetail(@PathVariable("id") Long orderId) {
        OrderVO orderDetails = orderService.getOrderDetails(orderId);
        return Result.success(orderDetails);
    }

    @GetMapping("/statistics")
    @ApiOperation("各个订单状态统计")
    public Result getStatistics() {
        OrderStatisticsVO statistics = orderService.getStatistics();
        return Result.success(statistics);
    }

    @PutMapping("/confirm")
    @ApiOperation("商家接单")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        int msgCode = orderService.confirmOrder(ordersConfirmDTO);
        // 不可能走到error 会抛异常
        return msgCode == 1 ? Result.success() : Result.error("接单失败");
    }

    @PutMapping("/rejection")
    @ApiOperation("商家拒单")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
         orderService.rejectOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliveryOrder(@PathVariable("id") Long orderId) {
        orderService.deliveryOrder(orderId);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result completeOrder(@PathVariable("id") Long orderId) {
        orderService.completeOrder(orderId);
        return Result.success();
    }

}
