package com.fdt.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ImportPlanetUserInfo {
    public static void main(String[] args) {
        String fileName="D:\\files\\idea_workplace\\user_center_fdt\\src\\main\\resources\\PlanetUserInfo.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<PlanetUserInfo> userInfoList =
                EasyExcel.read(fileName).head(PlanetUserInfo.class).sheet().doReadSync();
        System.out.println("用户数量="+userInfoList.size());
        Map<String,List<PlanetUserInfo>> listMap=
//              将用户昵称-username重复的用户分成一组，键为用户昵称，值为用户昵称相同的用户，查看不重复昵称的用户数量
//                我们判断是否为空是判断用户昵称是否为空，不是整条数据是否为空，所以不用使用Objects.nonNull()，而是使用Objects.nonNull()
//                userList.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(PlanetUserInfo::getUsername));
                userInfoList.stream()
                        .filter(userInfo-> StringUtils.isNotEmpty(userInfo.getUsername()))
                        .collect(Collectors.groupingBy(PlanetUserInfo::getUsername));
        System.out.println("昵称不重复用户数量="+listMap.size());
        for (Map.Entry<String,List<PlanetUserInfo>> stringListEntry : listMap.entrySet()){
            String key=stringListEntry.getKey();
            System.out.println("username="+key);
            System.out.println(1);
        }
    }
}
