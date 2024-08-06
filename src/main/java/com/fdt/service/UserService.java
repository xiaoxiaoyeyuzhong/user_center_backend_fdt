package com.fdt.service;

import com.fdt.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 根据标签搜索用户
     * @param tagNameList 标签列表
     * @return List<User> 用户列表
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 用户注销
     * @param request
     * @return void
     */
    public int userLogout(HttpServletRequest request);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return int
     */
    int updateUser(User user,User loginUser);

    /**
     * 获取当前登录用户
     * @param request 请求信息
     * @return User 当前登录用户信息
     */
    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);
}
