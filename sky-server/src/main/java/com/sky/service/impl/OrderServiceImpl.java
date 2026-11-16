package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/16 10:54
 * @description:none
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        // 处理 地址簿异常、购物车数据异常
        if(ordersSubmitDTO.getAddressBookId()==null)
            throw new IllegalStateException("传入地址簿id为空");

        Long addressBookId=ordersSubmitDTO.getAddressBookId();
        if(addressBookMapper.getById(addressBookId)==null)
            throw new IllegalStateException("订单暂未添加地址");

        Long curUserId= BaseContext.getCurrentId(); //获取当前下单用户id
        List<ShoppingCart> shoppingCart = shoppingCartMapper.getShoppingCart(curUserId);
        if(shoppingCart.isEmpty())
            throw new IllegalStateException("当前用户购物车为空");

        // 订单表插入1条数据
        Orders newOrder=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,newOrder);
        newOrder.setStatus(Orders.PENDING_PAYMENT);   //初始化订单为待付款状态

        int msgCode = orderMapper.insertOrder(newOrder);
        if(msgCode!=1) throw new IllegalStateException("插入订单失败");

        Long orderId= newOrder.getId();     //插入成功返回的主键值


        // 订单明细表插入n条数据(该订单对应菜品/套餐)  (菜品从哪而来？获取当前用户购物车)
        //TODO 根据userId查询购物车 插入orderDetails


        // 清空当前用户购物车


        return null;
    }
}
