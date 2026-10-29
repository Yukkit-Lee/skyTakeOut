package com.sky.service;

/**
 * @author: 44219
 * @since: 2026/10/29 10:55
 * @description:none
 */
public interface ShopService {

    /** 设置店铺营业状态 */
    int setStatus(Integer status);

    /** 查询店铺营业状态 */
    int getStatus();
}
