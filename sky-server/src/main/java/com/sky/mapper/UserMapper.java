package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

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


    @Select("SELECT * FROM user WHERE id= #{userId}")
    User getById(Long userId);

    /** 统计创建时间符合区间的用户 */
    List<User> getUserByDuration(LocalDateTime begin, LocalDateTime end);

    /** 获取指定日期前用户数 */
    @Select("SELECT COUNT(*) FROM user WHERE create_time < #{curTime}")
    Integer getCountBeforeDuration(LocalDateTime curTime);
}
