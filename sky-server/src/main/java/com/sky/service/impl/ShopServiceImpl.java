package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * @author: 44219
 * @since: 2026/10/29 10:56
 * @description:none
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public int setStatus(Integer status) {

        // value 使用字符串序列化器，因此这里也按字符串读写状态
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // 将状态保存为 "0" 或 "1"
        valueOperations.set("status", status.toString());

        String redisStatus = valueOperations.get("status");

        // 比较传入参数与redis中是否一致检验成功与否
        if (redisStatus != null && redisStatus.equals(status.toString()))
            return 1;

        return 0;
    }

    @Override
    public int getStatus() {

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String status = valueOperations.get("status");

        if (status != null)
            // Redis 中保存的是字符串，将其转回整数状态返回
            return Integer.parseInt(status);

        return 0;
    }
}
