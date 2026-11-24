package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @author: 44219
 * @since: 2026/11/22 18:15
 * @description:none
 */
public interface ReportService {

    /** 统计营业额 */
    TurnoverReportVO getTurnOver(LocalDate begin,LocalDate end);

    /** 统计用户量数据 */
    UserReportVO getUserOver(LocalDate begin,LocalDate end);

    /** 统计订单情况 */
    OrderReportVO getOrdersData(LocalDate begin, LocalDate end);

    /** 统计销量TOP10菜品/套餐 */
    SalesTop10ReportVO getTopTen(LocalDate begin, LocalDate end);

    /** 导出运营数据报表 */
    void exportData(HttpServletResponse response);
}
