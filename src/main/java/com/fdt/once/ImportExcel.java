package com.fdt.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 导入 Excel
 *
 * @author fdt
 */
public class ImportExcel {
    public static void main(String[] args) {
        String fileName="D:\\files\\idea_workplace\\user_center_fdt\\src\\main\\resources\\PlanetUserInfo.xlsx";
//        complexHeaderRead(fileName);
        synchronousRead(fileName);
    }

    public static void complexHeaderRead(String filename){
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = filename;
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, PlanetUserInfo.class, new TableDataListener()).sheet().doRead();
    }

    public static void synchronousRead(String filename) {
        String fileName = filename;
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<PlanetUserInfo> totalDataList =
                EasyExcel.read(fileName).head(PlanetUserInfo.class).sheet().doReadSync();
        for (PlanetUserInfo data : totalDataList) {
            System.out.println(data);
        }
    }
}
