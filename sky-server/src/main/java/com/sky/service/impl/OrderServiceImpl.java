package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
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

    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";

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
        if (ordersSubmitDTO.getAddressBookId() == null)
            throw new IllegalStateException("传入地址簿id为空");

        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null)
            throw new IllegalStateException("订单暂未添加地址");

        Long curUserId = BaseContext.getCurrentId(); //获取当前下单用户id
        List<ShoppingCart> shoppingCart = shoppingCartMapper.getShoppingCart(curUserId);
        if (shoppingCart.isEmpty())
            throw new IllegalStateException(MessageConstant.SHOPPING_CART_IS_NULL);

        // 订单表插入1条数据
        Orders newOrder = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, newOrder);
        // 此处应该插入user_id到newOrder对象中(全局异常处理器捕获 异常信息封装到Result中 状态码返回200)
        newOrder.setUserId(curUserId);  //设置当前下单用户id
        newOrder.setStatus(Orders.PENDING_PAYMENT);   //初始化订单为待付款状态
        newOrder.setPayStatus(Orders.UN_PAID);  //初始化订单为未支付
        newOrder.setPhone(addressBook.getPhone());  //从地址簿获取手机号
        newOrder.setConsignee(addressBook.getConsignee());
        newOrder.setNumber(String.valueOf(System.currentTimeMillis())); //设置订单号
        newOrder.setOrderTime(LocalDateTime.now());     //初始化下单时间

        // 拼接地址字符串到order表address字段中
        String orderAddress = addressBook.getProvinceName() + addressBook.getCityName() + addressBook
                .getDistrictName() + addressBook.getDetail();
        newOrder.setAddress(orderAddress);

        int msgCode = orderMapper.insertOrder(newOrder);
        if (msgCode != 1) throw new IllegalStateException("插入订单失败");

        Long orderId = newOrder.getId();     //插入成功返回的主键值


        // 订单明细表插入n条数据(该订单对应菜品/套餐)  (菜品从哪而来？获取当前用户购物车)
        //TODO 根据userId查询购物车 插入orderDetails
        List<OrderDetail> detailsList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCart) {
            OrderDetail curDetail = new OrderDetail();

            // 购物车信息 -> details信息
            BeanUtils.copyProperties(cart, curDetail);
            curDetail.setOrderId(orderId);  // 额外添加一个order_id字段即可

            detailsList.add(curDetail);
        }

        int insertDetailsRows = orderDetailMapper.insertMultiDetails(detailsList);
        if (insertDetailsRows != detailsList.size())
            throw new IllegalStateException("存在未插入订单明细数据");

        // 清空当前用户购物车
        int cartCleanRows = shoppingCartMapper.cleanShoppingCart(curUserId);
        if (cartCleanRows != shoppingCart.size())
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
        JSONObject jsonObject = new JSONObject();

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

    @Override
    public PageResult getUserOrdersByPage(OrdersPageQueryDTO ordersPageQueryDTO) {

        // 用户端查询订单信息 需要绑定userId进行查询
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        // 开启分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> orderInfo = orderMapper.getOrderInfo(ordersPageQueryDTO);

        List<OrderVO> voList = transferOrderVO(orderInfo, ROLE_USER);

        // pageResult构造方法中(pages总数，pages对象数据)
        PageResult pageResult = new PageResult(orderInfo.getTotal(), voList);
        return pageResult;

    }

    @Override
    public PageResult getAdminOrdersByPage(OrdersPageQueryDTO ordersPageQueryDTO) {

        // 开启分页
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> orderInfo = orderMapper.getOrderInfo(ordersPageQueryDTO);

        List<OrderVO> voList = transferOrderVO(orderInfo, ROLE_ADMIN);

        // pageResult构造方法中(pages总数，pages对象数据)
        PageResult pageResult = new PageResult(orderInfo.getTotal(), voList);
        return pageResult;

    }

    @Override
    public OrderStatisticsVO getStatistics() {


        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        Integer toConfirmNum = orderMapper.getStatusCount(Orders.TO_BE_CONFIRMED);
        orderStatisticsVO.setToBeConfirmed(toConfirmNum);
        Integer deliveryNum = orderMapper.getStatusCount(Orders.DELIVERY_IN_PROGRESS);
        orderStatisticsVO.setDeliveryInProgress(deliveryNum);
        Integer confirmNum = orderMapper.getStatusCount(Orders.CONFIRMED);
        orderStatisticsVO.setConfirmed(confirmNum);

        return orderStatisticsVO;

    }

    @Override
    @Transactional
    public int confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {

        // 考虑业务逻辑：什么情况下可以接单?
        // 2待接单->可接单 1待付款->用户端需付款 其他状态抛异常

        if (ordersConfirmDTO == null || ordersConfirmDTO.getId() == null)
            throw new IllegalStateException("前端传入orderId为空");

        Orders curOrder = orderMapper.getOrderById(ordersConfirmDTO.getId());
        if (curOrder == null)
            throw new IllegalStateException("订单不存在");

        if (curOrder.getPayStatus() == Orders.PAID && curOrder.getStatus() == Orders.TO_BE_CONFIRMED) {
            curOrder.setStatus(Orders.CONFIRMED);   //更改订单状态为已接单
            int affectRows = orderMapper.updateOrderInfo(curOrder);
            if (affectRows != 1)
                throw new IllegalStateException("更改订单状态失败!");
        } else
            throw new IllegalStateException("接单失败:未支付或已经接单");

        return 1;
    }

    @Override
    @Transactional
    public int rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {

        if (ordersRejectionDTO == null)
            throw new IllegalStateException("DTO对象为空");

        // 业务逻辑: 拒单之后进行退款

        Orders order = orderMapper.getOrderById(ordersRejectionDTO.getId());
        if (order == null)
            throw new IllegalStateException("订单不存在");

        if (order.getStatus() != Orders.TO_BE_CONFIRMED)
            throw new IllegalStateException("订单状态异常");

        order.setStatus(Orders.CANCELLED);
        // 此处调用wx退款接口....
        order.setPayStatus(Orders.REFUND);
        order.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        order.setCancelTime(LocalDateTime.now());

        int affectRows = orderMapper.updateOrderInfo(order);
        if (affectRows != 1)
            throw new IllegalStateException("更改订单状态失败");

        return 1;
    }

    @Override
    public int cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        // 业务逻辑: 待派送4 派送中5 可以取消

        if (ordersCancelDTO == null || ordersCancelDTO.getId() == null)
            throw new IllegalStateException("前端传入DTO为空");

        Orders orderById = orderMapper.getOrderById(ordersCancelDTO.getId());
        if (orderById == null)
            throw new IllegalStateException("订单不存在");

        // 订单未接单/未在派送中
        if (orderById.getStatus() != Orders.DELIVERY_IN_PROGRESS
                && orderById.getStatus() != Orders.CONFIRMED)
            throw new IllegalStateException("订单状态异常取消失败");

        orderById.setCancelReason(ordersCancelDTO.getCancelReason());
        orderById.setCancelTime(LocalDateTime.now());
        orderById.setStatus(Orders.CANCELLED);
        orderById.setPayStatus(Orders.REFUND);

        int affectRows = orderMapper.updateOrderInfo(orderById);
        if (affectRows != 1)
            throw new IllegalStateException("修改订单状态异常!");

        return 1;
    }

    @Override
    public void deliveryOrder(Long orderId) {

        // 业务逻辑: 先判断前端参数是否为空 -> 订单是否存在
        // 订单状态必须为 已接单 Status=3
        if(orderId==null)
            throw new IllegalStateException("orderId为空");

        Orders order = orderMapper.getOrderById(orderId);
        if(order==null||order.getStatus()!=Orders.CONFIRMED)
            throw new IllegalStateException("订单不存在或状态异常");

        order.setStatus(Orders.DELIVERY_IN_PROGRESS);

        int affectRows = orderMapper.updateOrderInfo(order);
        if(affectRows!=1)
            throw new IllegalStateException("更新订单状态失败!");
    }

    @Override
    public void completeOrder(Long orderId) {
        // 业务逻辑: 先判断前端参数是否为空 -> 订单是否存在
        // 订单状态必须为 派送中 Status=4
        if(orderId==null)
            throw new IllegalStateException("orderId为空");

        Orders order = orderMapper.getOrderById(orderId);
        if(order==null||order.getStatus()!=Orders.DELIVERY_IN_PROGRESS)
            throw new IllegalStateException("订单不存在或状态异常");

        order.setStatus(Orders.COMPLETED);

        int affectRows = orderMapper.updateOrderInfo(order);
        if(affectRows!=1)
            throw new IllegalStateException("更新订单状态失败!");
    }

    @Override
    public OrderVO getOrderDetails(Long orderId) {

        if (orderId == null)
            return null;
        // 根据订单id查询 orders 并 查询 ordersDetail 组成VO
        Orders order = orderMapper.getOrderById(orderId);
        if (order == null)
            throw new IllegalStateException("不存在的订单!");

        List<OrderDetail> details = orderDetailMapper.getDetailsByOrderId(orderId);
        if (details.isEmpty())
            throw new IllegalStateException("订单下无菜品!");

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(details);

        return orderVO;
    }

    @Override
    public void cancelOrder(Long orderId) {

        // 业务规则：订单处于 1.2 用户可以直接取消订单 3.4 已接单配送中需要联系商家抛异常
        Orders curOrder = orderMapper.getOrderById(orderId);
        if (curOrder == null)
            throw new IllegalStateException("未查询到有效订单!");

        Integer status = curOrder.getStatus();

        if (status > 2)
            throw new IllegalStateException("订单已取消或联系商家处理!");
        // 待接单状态取消需要进行退款
        if (status == Orders.TO_BE_CONFIRMED) {
            // 调用微信支付退款接口...
            curOrder.setPayStatus(Orders.REFUND);   //设置支付状态为退款
        }

        curOrder.setStatus(Orders.CANCELLED);
        curOrder.setCancelReason("用户取消订单");
        curOrder.setCancelTime(LocalDateTime.now());

        orderMapper.updateOrderInfo(curOrder);

    }

    @Override
    public void repetitionOrder(Long orderId) {

        // 先查当前orderId是否存在数据库中
        Orders curOrder = orderMapper.getOrderById(orderId);
        if (curOrder == null)
            throw new IllegalStateException("当前订单数据不存在");
        // 重新放入购物车即可
        // 根据orderId查询orderDetail表 插入 shoppingCart表
        List<OrderDetail> details = orderDetailMapper.getDetailsByOrderId(orderId);
        List<ShoppingCart> cartList = new ArrayList<>();
        for (OrderDetail detail : details) {
            ShoppingCart curCart = new ShoppingCart();
            BeanUtils.copyProperties(detail, curCart);

            // details没有创建时间和userId
            curCart.setCreateTime(LocalDateTime.now());
            curCart.setUserId(BaseContext.getCurrentId());
            cartList.add(curCart);
        }

        int affectRows = shoppingCartMapper.insertMultiCart(cartList);
        if (affectRows != cartList.size())
            throw new IllegalStateException("重新添加至购物车失败!");
    }


    /**
     * 辅助函数：用户端管理端抽离转换OrdersVO对象
     */
    private List<OrderVO> transferOrderVO(Page<Orders> orderInfo, String role) {
        List<OrderVO> voList = new ArrayList<>();

        // 查询到订单记录
        if (orderInfo != null && orderInfo.size() != 0) {
            for (Orders orders : orderInfo) {
                Long curOrderId = orders.getId(); //获取当前订单id
                List<OrderDetail> details = orderDetailMapper.getDetailsByOrderId(curOrderId);

                OrderVO newOrderVo = new OrderVO();
                BeanUtils.copyProperties(orders, newOrderVo);   //这里注意ordersVO和orders继承关系

                // 遵循接口文档返回参数规则：
                if (role == ROLE_USER)
                    newOrderVo.setOrderDetailList(details);
                else if (role == ROLE_ADMIN) {
                    String dishStr = buildOrdersString(details);
                    newOrderVo.setOrderDishes(dishStr);
                }
                voList.add(newOrderVo);
            }

        }
        return voList;
    }

    /**
     * 辅助函数:拼接OrdersVO对象的orderDIshes变量 用于字符串展示菜品
     */
    private String buildOrdersString(List<OrderDetail> details) {
        List<String> dishList = new ArrayList<>();
        for (OrderDetail detail : details) {
            dishList.add(detail.getName() + '*' + detail.getNumber() + ';');
        }
        return String.join("", dishList);
    }
}
