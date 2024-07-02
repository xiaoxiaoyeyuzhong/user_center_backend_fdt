package com.fdt.common;

import lombok.Data;

/**
 * 错误码
 *
 * @author fdt
 */
public enum ErrorCode {

    SUCCESS(0, "ok", ""),
//    400表示用户端有问题
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100,"用户未登录",""),
    NO_AUTH(40101, "用户无权限", ""),
    SYSTEM_ERROR(50000, "系统内部异常", "");
    private final int code;

    //错误码信息
    private final String message;

    //错误码详细描述
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public String description() {
        return description;
    }
}
