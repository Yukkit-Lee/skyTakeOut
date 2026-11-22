package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

/**
 * @author: 44219
 * @since: 2026/11/22 18:15
 * @description:none
 */
public interface ReportService {

    /** 统计营业额 */
    TurnoverReportVO getTurnOver(LocalDate begin,LocalDate end);

}
