package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderDetailTopVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author: 44219
 * @since: 2026/11/16 10:54
 * @description:none
 */
@Mapper
public interface OrderMapper {

    /** 插入订单数据 */
    int insertOrder(Orders orders);

    /** 获取订单信息 */
    Page<Orders> getOrderInfo(OrdersPageQueryDTO ordersPageQueryDTO);
    // 这里参数为什么要放DTO对象？ 是为了PageHelper把分页参数传入吗
    // 管理端接口 可复用

    /** 获取单条订单信息 */
    Orders getOrderById(Long orderId);

    /** 修改订单信息 */
    int updateOrderInfo(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /** 查询订单数量统计 */
    Integer getStatusCount(Integer status);

    /** 定时任务查询订单(查询超时订单) */
    @Select("SELECT * FROM orders WHERE status=#{status} AND order_time < #{time}")
    List<Orders> getOrderByStatusAndTime(Integer status, LocalDateTime time);

    /** 统计符合时间区间的有效订单 */
    // 用between要注意 between (end 23:59:59)
    List<Orders> getCompleteOrdersByTime(LocalDateTime begin, LocalDateTime end);

    /** 统计符合区间的订单 */
    List<Orders> getOrdersByTime(LocalDateTime begin, LocalDateTime end);

    /** 统计开始时间前订单数量,status为空时统计全部 */
    Integer getNumBeforeTime(LocalDateTime begin,Integer status);

    /** 多表查询Top10菜品数据 符合时间区间且 status=5 */
    List<OrderDetailTopVO> getTopTen(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);
}
