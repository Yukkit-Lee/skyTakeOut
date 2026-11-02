package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;
import org.springframework.stereotype.Service;

/**
 * @author: 44219
 * @since: 2026/11/1 10:07
 * @description:none
 */
public interface UserService {

    /** 用户登录返回openid,jwt token */
    UserLoginVO wxLogin(UserLoginDTO userLoginDTO);

}
