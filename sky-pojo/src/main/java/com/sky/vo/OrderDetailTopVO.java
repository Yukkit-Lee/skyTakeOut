package com.sky.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: 44219
 * @since: 2026/11/23 18:48
 * @description:none
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailTopVO implements Serializable {

    // 菜品名称
    private String name;

    // 当前菜品销量
    private Integer totalNumber;

}
