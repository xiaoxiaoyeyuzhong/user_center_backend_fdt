package com.fdt;

import com.fdt.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;
    @Test
    void testRedis() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("tianString","fdt");
        valueOperations.set("tianInt",1);
        valueOperations.set("tianDouble",2.0);
        User user=new User();
        user.setId(1L);
        user.setUserAccount("fdt");
        valueOperations.set("userTian",user);
        //查
        Object tian = valueOperations.get("tianString");
        Assertions.assertTrue("fdt".equals((String)tian));
        tian = valueOperations.get("tianInt");
        Assertions.assertTrue(1==(Integer)tian);
        tian = valueOperations.get("tianDouble");
        Assertions.assertTrue(2.0==(Double)tian);
        System.out.println(valueOperations.get("userTian"));
//        改

        valueOperations.set("tianString","fdt2");
        tian = valueOperations.get("tianString");
        Assertions.assertTrue("fdt2".equals((String)tian));
        //int 类型
        valueOperations.set("tianInt",1280);
        tian = valueOperations.get("tianInt");
        Assertions.assertTrue(1280==(Integer)tian);
        //Double 类型
        valueOperations.set("tianDouble",2.0);
        tian = valueOperations.get("tianDouble");
        Assertions.assertTrue(2.0==(Double)tian);
        //删
        redisTemplate.delete("tianString");
        redisTemplate.delete("tianInt");
        redisTemplate.delete("tianDouble");
    }

}
