package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: 44219
 * @since: 2026/11/16 11:04
 * @description:none
 */
@Mapper
public interface OrderDetailMapper {

    /** 插入订单明细 */
    int insertDetails(OrderDetail orderDetail);

}
