package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: 44219
 * @since: 2026/10/29 10:49
 * @description:none
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result getStatus() {
        int status = shopService.getStatus();
        return Result.success(status);
    }

}
