package com.fdt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fdt.model.domain.User;
import com.fdt.service.UserService;
import com.fdt.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.fdt.contant.UserContant.USER_LOGIN_STATE;

/**
* @author 冯德田
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-05-12 23:04:46
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "fdt";


    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 重复密码
     * @param planetCode 星球编号
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        // 1. 校验
        // 不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)){
            return -1;
        }
        //账户长度不能小于4
        if (userAccount.length() < 4){
            return -1;
        }
        //密码和重复长度不能小于8
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }

        // 星球编号长度不大于5
        if(planetCode.length() > 5){
            return -1;
        }

        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z0-9]+$";
        if (!userAccount.matches(validPattern)){
            return -1;
        }

        // 密码和校验密码相同，不能用等于判断两个字符串是否相同
        if (!userPassword.equals(checkPassword)){
            return -1;
        }

        // 账户不能重复，数据库操作放后面，避免性能浪费，
        // 如果其他规则都没通过，就不需要判断账户是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            return -1;
        }

        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            return -1;
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 保存到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }


    /**
     * 登录逻辑
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        // 不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            // todo 修改为自定义异常
            return null;
        }
        //账户长度不能小于4
        if (userAccount.length() < 4){
            return null;
        }
        //密码长度不能小于8
        if (userPassword.length() < 8){
            return null;
        }

        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z0-9]+$";
        if (!userAccount.matches(validPattern)){
            return null;
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed,userAccount cannot match userPassword");
            return null;
        }



        //4.脱敏
        User safetyUser = getSafetyUser(user);

        //5.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 获取脱敏用户
     * @param orginUser
     * @return
     */
    @Override
    public User getSafetyUser(User orginUser){
        if (orginUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(orginUser.getId());
        safetyUser.setUsername(orginUser.getUsername());
        safetyUser.setUserAccount(orginUser.getUserAccount());
        safetyUser.setAvatarUrl(orginUser.getAvatarUrl());
        safetyUser.setGender(orginUser.getGender());
        safetyUser.setPhone(orginUser.getPhone());
        safetyUser.setEmail(orginUser.getEmail());
        safetyUser.setUserRole(orginUser.getUserRole());
        safetyUser.setStatus(orginUser.getStatus());
        safetyUser.setCreateTime(orginUser.getCreateTime());
        safetyUser.setPlanetCode(orginUser.getPlanetCode());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
//        移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




