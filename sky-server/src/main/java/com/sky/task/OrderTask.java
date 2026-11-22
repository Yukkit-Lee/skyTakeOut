package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/22 10:22
 * @description:定时任务类:定时处理订单状态
 */
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时未付款订单
     */
    // 秒 分 时 日 月 周 年
    @Scheduled(cron = "0 * * * * ?")    //每分钟执行一次
//    @Scheduled(cron = "*/10 * * * * ?")    //每10s
    public void processTimeoutOrder() {

        //  针对超时未支付订单 设置15分钟期限  (now-15)>order_time即为超时
        LocalDateTime timeScale = LocalDateTime.now().plusMinutes(-15);

        List<Orders> orders = orderMapper.getOrderByStatusAndTime(Orders.PENDING_PAYMENT, timeScale);
        if (!orders.isEmpty()) {
            for (Orders order : orders) {
                order.setStatus(Orders.CANCELLED);  //取消订单
                order.setCancelReason("超时自动取消");
                order.setCancelTime(LocalDateTime.now());

                orderMapper.updateOrderInfo(order);
            }
        }

    }

    /**
     * 处理已派送完没点完成订单
     */
    @Scheduled(cron = "0 0 1 * * ?")    //每日01:00
//    @Scheduled(cron = "*/20 * * * * ?")    //每20s
    public void processCompleteOrder() {

        LocalDateTime timeScale = LocalDateTime.now().plusMinutes(-60);

        List<Orders> orders = orderMapper.getOrderByStatusAndTime(Orders.DELIVERY_IN_PROGRESS, timeScale);
        if (!orders.isEmpty()) {
            for (Orders order : orders) {
                order.setStatus(Orders.COMPLETED);  //完成订单
                orderMapper.updateOrderInfo(order);
            }
        }

    }


}
