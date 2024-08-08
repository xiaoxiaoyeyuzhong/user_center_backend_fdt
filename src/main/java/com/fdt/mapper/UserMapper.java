package com.fdt.mapper;

import com.fdt.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 冯德田
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-05-12 23:04:46
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




