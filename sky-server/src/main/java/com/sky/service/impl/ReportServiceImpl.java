package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 44219
 * @since: 2026/11/22 18:15
 * @description:none
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO getTurnOver(LocalDate begin, LocalDate end) {

        if (begin == null || end == null)
            throw new IllegalStateException("存在前端传入数据为空");

        // 转换为LocalDateTime begin的最小时间点
        LocalDateTime beginToMapper = LocalDateTime.of(begin, LocalTime.MIN);
        // 转换为LocalDateTime end的最大时间点
        LocalDateTime endToMapper = LocalDateTime.of(end, LocalTime.MAX);


        // begin自加一天直到等于end
        List<LocalDate> dateList = buildDateList(begin, end);

        // 业务逻辑: 前端传 beginTime endTime -> 查order表统计amount orderTime
        List<Orders> orders = orderMapper.getCompleteOrdersByTime(beginToMapper, endToMapper);


        // 初始化每天营业额Map 用LinkedHashMap存储
        Map<LocalDate, BigDecimal> turnOverMap = new LinkedHashMap<>();

        // 先存入dateList中日期
        for (LocalDate date : dateList) {
            turnOverMap.put(date, BigDecimal.ZERO);
        }

        // 同一天营业额算到一起,begin-end按天分隔
        for (Orders order : orders) {
            // 先将数据库date对象转换为LocalDate
            LocalDate curDate = order.getOrderTime().toLocalDate();

            if (turnOverMap.containsKey(curDate)) {
                BigDecimal curDateAmount = turnOverMap.get(curDate);   //同一日金额

                turnOverMap.put(curDate, curDateAmount.add(order.getAmount()));  //bigDecimal用add加
            }
        }

        String dateListStr = StringUtils.join(dateList, ",");
        // 通过StringBuilder构造turnOverStr
        StringBuilder turnoverBuilder = new StringBuilder();
        for (BigDecimal value : turnOverMap.values()) {
            if (turnoverBuilder.length() > 0)
                turnoverBuilder.append(",");
            turnoverBuilder.append(value);
        }

        // 构造VO对象
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(dateListStr)
                .turnoverList(turnoverBuilder.toString())
                .build();

        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserOver(LocalDate begin, LocalDate end) {

        // 业务逻辑: newUserList存放当天新增用户,totalUserList存放累计用户

        // 1.构造LocalDateTime对象 构造Map<LocalDate,newUserNum>
        LocalDateTime beginToMapper = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endToMapper = LocalDateTime.of(end, LocalTime.MAX);
        LocalDateTime beginAtStartDay = begin.atStartOfDay();

        // 2.构造dateList 11.02 11.03 11.04 11.05 ... 11.23
        List<LocalDate> dateList = buildDateList(begin, end);

        // <11.02 0>,<11.03 0>.....,<11.23 0>
        Map<LocalDate, Integer> newUserMap = new LinkedHashMap<>();
        for (LocalDate localDate : dateList) {
            newUserMap.put(localDate, 0);
        }

        // 3.mapper层查询
        List<User> userList = userMapper.getUserByDuration(beginToMapper, endToMapper);
        for (User user : userList) {
            LocalDate curDate = user.getCreateTime().toLocalDate();
            if (newUserMap.containsKey(curDate)) {
                Integer curNum = newUserMap.get(curDate);
                newUserMap.put(curDate, curNum + 1);
            }
        }
        // -> <11.02 3>,<11.03 0>.....,<11.23 1>

        //只查询开始时间当天前user总数 只查一次数据库 后续循环中累加
        List<Integer> totalUserList = new ArrayList<>();
        Integer beforeNum = userMapper.getCountBeforeDuration(beginAtStartDay);
        if (beforeNum == null)
            beforeNum = 0;
        Integer totalCount = beforeNum;

        for (LocalDate localDate : dateList) {
            Integer curDayNewUser = newUserMap.get(localDate);    //当天新增用户

            totalCount += curDayNewUser;
            totalUserList.add(totalCount);
        }

        // 转换为接口要求的逗号分隔字符串
        String dateListStr = StringUtils.join(dateList, ",");

        String newUserListStr =
                StringUtils.join(newUserMap.values(), ",");

        String totalUserListStr =
                StringUtils.join(totalUserList, ",");

        return UserReportVO.builder()
                .dateList(dateListStr)
                .newUserList(newUserListStr)
                .totalUserList(totalUserListStr)
                .build();
    }

    @Override
    public OrderReportVO getOrdersData(LocalDate begin, LocalDate end) {

        // 业务逻辑: 每日订单数 每日有效订单数 -> 查id,status  (区间内)
        // 订单总数 有效订单数 ->  查count id (区间内)

        if (begin == null || end == null)
            return null;

        LocalDateTime beginToMapper = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endToMapper = LocalDateTime.of(end, LocalTime.MAX);
        LocalDateTime beginAtStartDay = begin.atStartOfDay();

        List<LocalDate> dateList = buildDateList(begin, end);


        // 构造（每日订单数 每日有效订单数） MAP
        Map<LocalDate, Integer> totalOrderMap = new LinkedHashMap<>();
        Map<LocalDate, Integer> completeOrderMap = new LinkedHashMap<>();
        for (LocalDate date : dateList) {
            totalOrderMap.put(date, 0);
            completeOrderMap.put(date, 0);
        }

        // 遍历mapper层 ->
        Integer validCountNum = 0;
        List<Orders> allOrders = orderMapper.getOrdersByTime(beginToMapper, endToMapper);
        for (Orders order : allOrders) {
            LocalDate curDate = order.getOrderTime().toLocalDate();
            if (totalOrderMap.containsKey(curDate))
                totalOrderMap.put(curDate, totalOrderMap.get(curDate) + 1);
            if (order.getStatus().equals(Orders.COMPLETED) && completeOrderMap.containsKey(curDate)) {
                completeOrderMap.put(curDate, completeOrderMap.get(curDate) + 1);
                validCountNum++;
            }
        }

        Integer totalOrderCount = allOrders.size();
        Double orderCompletionRate = (double) validCountNum / totalOrderCount;


        String dateListStr = StringUtils.join(dateList, ",");
        String orderCountList = StringUtils.join(totalOrderMap.values(), ",");
        String validList = StringUtils.join(completeOrderMap.values(), ",");


        return OrderReportVO.builder()
                .dateList(dateListStr)
                .orderCountList(orderCountList)
                .validOrderCountList(validList)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validCountNum)
                .orderCompletionRate(orderCompletionRate)
                .build();

    }

    @Override
    public SalesTop10ReportVO getTopTen(LocalDate begin, LocalDate end) {

        /* 业务逻辑总览:
         * 根据(begin,end)区间在order表中查到 id
         * 根据order_id 到 order_detail表中 查询
         * GROUP BY name
         * select name,sum(number)
         *
         * nameList 逗号分隔，numberList逗号分隔
         * */

        if (begin == null || end == null)
            return null;

        // 构造LocalDateTime对象
        LocalDateTime beginToMapper = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endToMapper = LocalDateTime.of(end, LocalTime.MAX);

        // 内连接多表查询
        List<OrderDetailTopVO> topTenList = orderMapper.getTopTen(beginToMapper, endToMapper);

        // Stream中map为映射（转换）函数:流中所有元素都要 按照规则变形 这里是 getName
        List<String> nameList = topTenList.stream().map(OrderDetailTopVO::getName)
                .collect(Collectors.toList());
        List<Integer> numberList = topTenList.stream().map(OrderDetailTopVO::getTotalNumber)
                .collect(Collectors.toList());

        String nameListStr=StringUtils.join(nameList,",");
        String numberListStr=StringUtils.join(numberList,",");

        return SalesTop10ReportVO.builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();

    }

    /**
     * 辅助函数：用于构造dateList
     */
    private List<LocalDate> buildDateList(LocalDate beginTime, LocalDate endTime) {
        List<LocalDate> dateList = new ArrayList<>();

        if (beginTime.isAfter(endTime))
            return dateList;

        dateList.add(beginTime);
        while (!beginTime.equals(endTime)) {
            beginTime = beginTime.plusDays(1);
            dateList.add(beginTime);
        }
        return dateList;
    }
}
