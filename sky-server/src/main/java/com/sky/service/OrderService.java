package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @author: 44219
 * @since: 2026/11/16 10:54
 * @description:none
 */
public interface OrderService {

    /** 用户提交订单 */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

}
