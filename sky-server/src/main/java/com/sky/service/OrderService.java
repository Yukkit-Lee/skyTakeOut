package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * @author: 44219
 * @since: 2026/11/16 10:54
 * @description:none
 */
public interface OrderService {

    /** 用户提交订单 */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /** 当前用户历史订单分页查询 */
    PageResult getUserOrdersByPage(OrdersPageQueryDTO ordersPageQueryDTO);

    /** 查询订单详情 */
    OrderVO getOrderDetails(Long orderId);

    /** 用户取消订单 */
    void cancelOrder(Long orderId);

    /** 再来一单 */
    void repetitionOrder(Long orderId);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /** 管理端: 历史订单分页查询 */
    PageResult getAdminOrdersByPage(OrdersPageQueryDTO ordersPageQueryDTO);

    /** 管理端: 获取订单状态数量 */
    OrderStatisticsVO getStatistics();

    /** 管理端: 接单 */
    int confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    /** 管理端: 拒单 */
    int rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    /** 管理端: 取消订单 */
    int cancelOrder(OrdersCancelDTO ordersCancelDTO);
    
    /** 管理端: 派送订单 */
    void deliveryOrder(Long orderId);

    /** 管理端: 完成订单 */
    void completeOrder(Long orderId);
}
