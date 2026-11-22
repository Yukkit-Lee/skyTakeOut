package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author: 44219
 * @since: 2026/11/22 18:15
 * @description:none
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public TurnoverReportVO getTurnOver(LocalDate begin,LocalDate end) {

        if (begin == null || end== null)
            throw new IllegalStateException("存在前端传入数据为空");

        // 转换为LocalDateTime begin的最小时间点
        LocalDateTime beginToMapper = LocalDateTime.of(begin, LocalTime.MIN);
        // 转换为LocalDateTime end的最大时间点
        LocalDateTime endToMapper = LocalDateTime.of(end, LocalTime.MAX);


        // begin自加一天直到等于end
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }


        // 业务逻辑: 前端传 beginTime endTime -> 查order表统计amount orderTime
        List<Orders> orders = orderMapper.getOrdersByDurationTime(beginToMapper, endToMapper);


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

        String dateListStr= StringUtils.join(dateList,",");
        // 通过StringBuilder构造turnOverStr
        StringBuilder turnoverBuilder=new StringBuilder();
        for (BigDecimal value : turnOverMap.values()) {
            if(turnoverBuilder.length()>0)
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
}
