package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import com.sky.service.impl.ShopServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: 44219
 * @since: 2026/10/29 10:49
 * @description:none
 */
@RestController("adminShopController")  //此处为了区分user的相同接口 —> spring容器中
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @ApiOperation("设置营业状态")
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        int msgCode = shopService.setStatus(status);
        return msgCode == 1 ? Result.success(msgCode) : Result.error("设置失败");
    }

    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result getStatus() {
        int status = shopService.getStatus();
        return Result.success(status);
    }

}
