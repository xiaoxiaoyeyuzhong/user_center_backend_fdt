package com.fdt.service;

import com.fdt.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 冯德田
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-05-12 23:04:46
*/
public interface UserService extends IService<User> {


    /**
     *用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 重复密码
     * @param planetCode 星球编号
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取脱敏后的用户信息
     * @param orginUser 原始用户信息
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User orginUser);

    /**
     * 用户注销
     * @param request
     * @return void
     */
    public int userLogout(HttpServletRequest request);
}
