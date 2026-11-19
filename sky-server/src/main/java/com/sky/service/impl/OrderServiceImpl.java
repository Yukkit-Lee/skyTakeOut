package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 44219
 * @since: 2026/11/16 10:54
 * @description:none
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        // 处理 地址簿异常、购物车数据异常
        if(ordersSubmitDTO.getAddressBookId()==null)
            throw new IllegalStateException("传入地址簿id为空");

        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null)
            throw new IllegalStateException("订单暂未添加地址");

        Long curUserId= BaseContext.getCurrentId(); //获取当前下单用户id
        List<ShoppingCart> shoppingCart = shoppingCartMapper.getShoppingCart(curUserId);
        if(shoppingCart.isEmpty())
            throw new IllegalStateException(MessageConstant.SHOPPING_CART_IS_NULL);

        // 订单表插入1条数据
        Orders newOrder=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,newOrder);
        // 此处应该插入user_id到newOrder对象中(全局异常处理器捕获 异常信息封装到Result中 状态码返回200)
        newOrder.setUserId(curUserId);  //设置当前下单用户id
        newOrder.setStatus(Orders.PENDING_PAYMENT);   //初始化订单为待付款状态
        newOrder.setPayStatus(Orders.UN_PAID);  //初始化订单为未支付
        newOrder.setPhone(addressBook.getPhone());  //从地址簿获取手机号
        newOrder.setConsignee(addressBook.getConsignee());
        newOrder.setNumber(String.valueOf(System.currentTimeMillis())); //设置订单号
        newOrder.setOrderTime(LocalDateTime.now());     //初始化下单时间

        int msgCode = orderMapper.insertOrder(newOrder);
        if(msgCode!=1) throw new IllegalStateException("插入订单失败");

        Long orderId= newOrder.getId();     //插入成功返回的主键值


        // 订单明细表插入n条数据(该订单对应菜品/套餐)  (菜品从哪而来？获取当前用户购物车)
        //TODO 根据userId查询购物车 插入orderDetails
        List<OrderDetail> detailsList=new ArrayList<>();
        for (ShoppingCart cart : shoppingCart) {
            OrderDetail curDetail = new OrderDetail();

            // 购物车信息 -> details信息
            BeanUtils.copyProperties(cart,curDetail);
            curDetail.setOrderId(orderId);  // 额外添加一个order_id字段即可

            detailsList.add(curDetail);
        }

        int insertDetailsRows = orderDetailMapper.insertMultiDetails(detailsList);
        if(insertDetailsRows!=detailsList.size())
            throw new IllegalStateException("存在未插入订单明细数据");
        
        // 清空当前用户购物车
        int cartCleanRows = shoppingCartMapper.cleanShoppingCart(curUserId);
        if(cartCleanRows!=shoppingCart.size())
            throw new IllegalStateException("存在未删除购物车数据");


        // 封装VO对象返回
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .orderTime(newOrder.getOrderTime())
                .orderNumber(newOrder.getNumber())
                .orderAmount(newOrder.getAmount())
                .id(newOrder.getId())
                .build();

        return orderSubmitVO;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject=new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }
}
