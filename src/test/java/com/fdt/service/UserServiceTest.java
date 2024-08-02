package com.fdt.service;

import com.fdt.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/*
用户服务测试
author：fdt
*/
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    public void testAddUser(){
        User user=new User();
        user.setUsername("dogFdt");
        user.setUserAccount("qq321");
        user.setAvatarUrl("https://profile-avatar.csdnimg.cn/default.jpg!1");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("321");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {

        //账号不能为空
        String userAccount = "";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        String planetCode = "12345";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        //密码不能为空
        userAccount = "12345";
        userPassword = "";
        checkPassword = "12345678";
        planetCode = "12345";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        //重复密码不能为空
        userAccount = "12345";
        userPassword = "12345678";
        checkPassword = "";
        planetCode = "12345";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        //账户不能小于4位
        userAccount = "";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "12345";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        //密码不能小于8位
        userAccount = "12345678";
        userPassword = "123456";
        checkPassword = "123456";
        planetCode = "12345";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        //账号不包含特殊字符
        userPassword = "%1234567";
        userAccount = "12345678";
        checkPassword = "12345678";
        planetCode = "12345";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        //密码和重复密码相同
        userAccount = "12345";
        userPassword = "12345678";
        checkPassword = "1237864";
        planetCode = "12345";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

        //账号不重复
        userAccount = "qq321";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "12345";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1, result);

//        userAccount = "77122220";
//        userPassword = "12345678";
//        checkPassword = "12345678";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        System.out.println("result="+result);
//        Assertions.assertTrue(result>0);
    }
//         测试成功
        @Test
         void testUserSucceeded(){
            String userAccount = "fdt192351";
            String userPassword = "12345678";
            String checkPassword = "12345678";
            String planetCode = "32346";
            long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
            System.out.println("result="+result);
            Assertions.assertTrue(result>0);
        }

        @Test
        void testSearchUsersByTags(){
            List<String> tagNameList= Arrays.asList("java","python");
            List<User> userList=userService.searchUsersByTags(tagNameList);
//          这个仅仅会判断userList是否为null，但是不关心size，即使size=0也成功
//            Assertions.assertNotNull(userList);
//          换成assertTrue，判断是否不为null且size不为0
            Assertions.assertTrue(userList != null && !userList.isEmpty(), "userList should not be null and should not be empty");

        }

    }
