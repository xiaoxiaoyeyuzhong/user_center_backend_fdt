package com.fdt.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PlanetUserInfo {
    /**
     * 星球编号
     */
    @ExcelProperty("星球编号")
    private String planetCode;

    /**
         * 用户昵称
     */
    @ExcelProperty("用户昵称")
    private String username;
}
