package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: 44219
 * @since: 2026/11/1 10:12
 * @description:none
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String Code2SessionUrl = "https://api.weixin.qq.com" +
            "/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    private String getWxOpenid(String onceCode){
        //构造请求体body发送请求
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", onceCode);
        paramMap.put("grant_type", "authorization_code");

        String response = HttpClientUtil.doGet(Code2SessionUrl, paramMap);
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalStateException("微信登录请求失败");
        }

        JSONObject responseJson= JSON.parseObject(response);
        String wxOpenId = responseJson == null ? null : responseJson.getString("openid");
        if (wxOpenId == null || wxOpenId.trim().isEmpty()) {
            // 只记录微信返回的错误信息，不输出可能包含 session_key 的完整响应
            log.warn("微信登录失败：errcode={}, errmsg={}",
                    responseJson == null ? null : responseJson.getInteger("errcode"),
                    responseJson == null ? null : responseJson.getString("errmsg"));
            throw new IllegalStateException("未获取到微信用户openid");
        }
        return wxOpenId;
    }


    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public UserLoginVO wxLogin(UserLoginDTO userLoginDTO) {

        //1.调用微信接口获取openId

        //wx.login()返回的一次性身份code
        String onceCode = userLoginDTO.getCode();

        String wxOpenId = getWxOpenid(onceCode);

        // 判断是否为新用户(是则新增)
        User user = userMapper.userLogin(wxOpenId);
        if (user == null){
            user=User.builder()
                    .createTime(LocalDateTime.now())
                    .openid(wxOpenId).build();
            int insertMsg = userMapper.insertNewUser(user);
            if(insertMsg == 0) throw new IllegalStateException("新用户插入失败");
        }

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(wxOpenId)
                .build();   // jwt令牌controller层赋值

        return userLoginVO;
    }

}
