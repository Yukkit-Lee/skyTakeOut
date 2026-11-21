package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.aspectj.weaver.ast.Or;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/16 11:04
 * @description:none
 */
@Mapper
public interface OrderDetailMapper {

    /** 插入订单明细 */
    int insertDetails(OrderDetail orderDetail);

    /** 插入多条订单明细 */
    int insertMultiDetails(List<OrderDetail> orderDetailList);

    /** 根据orderId查询菜品明细 */
    List<OrderDetail> getDetailsByOrderId(Long orderId);
}
