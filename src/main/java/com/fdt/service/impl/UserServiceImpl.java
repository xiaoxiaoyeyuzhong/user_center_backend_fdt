package com.fdt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fdt.common.ErrorCode;
import com.fdt.exception.BusinessException;
import com.fdt.model.domain.User;
import com.fdt.service.UserService;
import com.fdt.mapper.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fdt.contant.UserContant.USER_LOGIN_STATE;

/**
 * @author 冯德田
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-05-12 23:04:46
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "fdt";


    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 重复密码
     * @param planetCode    星球编号
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. 校验
        // 不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码为空");
        }
        //账户长度不能小于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能小于4位");
        }
        //密码和重复长度不能小于8
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能小于8位");
        }

        // 星球编号长度不大于5
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号长度不大于5位");
        }

        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z0-9]+$";
        if (!userAccount.matches(validPattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }

        // 密码和校验密码相同，不能用等于判断两个字符串是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码应相同");
        }

        // 账户不能重复，数据库操作放后面，避免性能浪费，
        // 如果其他规则都没通过，就不需要判断账户是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号不能重复");
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 保存到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数据库保存用户注册信息失败");
        }
        return user.getId();
    }


    /**
     * 登录逻辑
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        // 不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码应相同");
        }
        //账户长度不能小于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度要大于4");
        }
        //密码长度不能小于8
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度要大于8");
        }

        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z0-9]+$";
        if (!userAccount.matches(validPattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }


        //4.脱敏
        User safetyUser = getSafetyUser(user);

        //5.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 获取脱敏用户
     *
     * @param orginUser
     * @return
     */
    @Override
    public User getSafetyUser(User orginUser) {
        if (orginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息为空");
        }
        User safetyUser = new User();
        safetyUser.setId(orginUser.getId());
        safetyUser.setUsername(orginUser.getUsername());
        safetyUser.setUserAccount(orginUser.getUserAccount());
        safetyUser.setAvatarUrl(orginUser.getAvatarUrl());
        safetyUser.setGender(orginUser.getGender());
        safetyUser.setPhone(orginUser.getPhone());
        safetyUser.setEmail(orginUser.getEmail());
        safetyUser.setTags(orginUser.getTags());
        safetyUser.setUserRole(orginUser.getUserRole());
        safetyUser.setStatus(orginUser.getStatus());
        safetyUser.setCreateTime(orginUser.getCreateTime());
        safetyUser.setPlanetCode(orginUser.getPlanetCode());
        return safetyUser;
    }

    /**
     * 根据标签获取用户
     * 在内存中进行过滤
     * @param tagNameList
     * @return List<User>
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
//      首先判断传入的标签列表是否为空
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //    2使用内存查询
//        2.1 先查询所有用户
//        查询开始时间
//        long startTime = System.currentTimeMillis();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
//        2.2 在内存中判断判断是否包含传入的标签
//        使用了lambda表达式 -> 指向动作
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
//            如果tagsStr为空，用户不存在标签，就无法通过标签查询用户，返回false
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
//          判断tempTagNameSet是否为空，java 8特性。
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tageName : tagNameList) {
                if (!tempTagNameSet.contains(tageName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
//        查询结束时间
//        log.info("内存查询用户耗时："+(System.currentTimeMillis() - startTime)+"ms");
    }

    /**
     * 根据标签获取用户
     * SQL查询版
     * @Deprecated 废弃接口
     * @param tagNameList
     * @return List<User>
     * private 设置成私有防止调用
     */
    @Deprecated
    private List<User> SQLsearchUsersByTags(List<String> tagNameList) {
//      首先判断传入的标签列表是否为空
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
////      查询开始时间
////      long startTime = System.currentTimeMillis();
////      使用SQL查询
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
////      1.拼接 and 查询，形式 like '%java%' and like '%python%'
//        for (String tagName : tagNameList){
////      1.2 使用like匹配tags列是否包含tagName
//            queryWrapper = queryWrapper.like("tags",tagName);
//        }
////      1.3 调用自带的selectList方法，将定义好的查询条件传入
//        List<User> userList = userMapper.selectList(queryWrapper);
//        /*
//        1.4 stream流式处理
//           map方法将每个User对象转换为safetyUser对象
//           collect方法将处理后的结果收集到一个新的List中
//        *  */
//        List<User> tempuserList=userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
////        查询结束时间
////        log.info("内存查询用户耗时："+(System.currentTimeMillis() - startTime)+"ms");
//        return tempuserList;


        //    2使用内存查询
//        2.1 先查询所有用户
//        查询开始时间
//        long startTime = System.currentTimeMillis();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
//        2.2 在内存中判断判断是否包含传入的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
//            如果tagsStr为空，用户不存在标签，就无法通过标签查询用户，返回false
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            for (String tageName : tagNameList) {
                if (!tempTagNameSet.contains(tageName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
//        查询结束时间
//        log.info("内存查询用户耗时："+(System.currentTimeMillis() - startTime)+"ms");
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
//        移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




