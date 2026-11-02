package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author: 44219
 * @since: 2026/11/2 10:16
 * @description:none
 */
@Mapper
public interface UserMapper {

    /** 根据openId查询用户 */
    @Select("SELECT * FROM user WHERE openid = #{wxOpenId}")
    User userLogin(String wxOpenId);

    /** 新增用户 */
    int insertNewUser(User user);
}
