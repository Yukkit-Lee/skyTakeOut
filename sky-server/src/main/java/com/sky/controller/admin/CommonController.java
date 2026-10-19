package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: 44219
 * @since: 2026/10/14 10:38
 * @description:none
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    // 此处形参名称要和前端保持一致
    public Result<String> uploadFile(MultipartFile file) {
        try {
            String originName = file.getOriginalFilename();   //添加UUID逻辑放在工具类
            String uploadUrl = aliOssUtil.upload(file.getBytes(), originName);
            return Result.success(uploadUrl);
        } catch (IOException e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }

}
