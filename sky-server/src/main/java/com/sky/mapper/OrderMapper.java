package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: 44219
 * @since: 2026/11/16 10:54
 * @description:none
 */
@Mapper
public interface OrderMapper {

    /** 插入订单数据 */
    int insertOrder(Orders orders);
}
