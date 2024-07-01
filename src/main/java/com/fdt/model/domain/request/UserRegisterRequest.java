package com.fdt.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author fdt
 */
@Data
public class UserRegisterRequest implements Serializable {
    // 防止序列化过程出现某些冲突
    private static final long serialVersionUID = 3191241716373120793L;
    // 用户账号
    private String userAccount;
    // 用户密码
    private String userPassword;
    // 重复密码
    private String checkPassword;
    //星球编号
    private String planetCode;
}
