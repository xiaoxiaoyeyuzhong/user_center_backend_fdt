package com.fdt.exception;

import com.fdt.common.ErrorCode;

/**
 * 自定义错误异常类
 *
 * @author fdt
 */
public class BusinessException extends RuntimeException{

    private final int code;

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
        this.description = errorCode.description();
    }

    public BusinessException(ErrorCode errorCode, String description){
        super(errorCode.message());
        this.code= errorCode.code();
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }
}
