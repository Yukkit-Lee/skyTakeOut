package com.sky.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 44219
 * @since: 2026/10/9 11:13
 * @description:none
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "分页查询员工返回数据")
public class EmployeePageVO implements Serializable {
    private Integer totalSize;
    private List empInfo;
}
