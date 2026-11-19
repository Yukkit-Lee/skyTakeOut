package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

/**
 * @author: 44219
 * @since: 2026/11/16 10:54
 * @description:none
 */
public interface OrderService {

    /** 用户提交订单 */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);


    /** 历史订单分页查询 */
    PageResult getOrdersByPage(OrdersPageQueryDTO ordersPageQueryDTO);


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
}
