package com.fdt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.fdt.common.BaseResponse;
import com.fdt.common.ErrorCode;
import com.fdt.common.ResultUtils;
import com.fdt.model.domain.request.UserLoginRequest;
import com.fdt.model.domain.request.UserRegisterRequest;
import com.fdt.service.UserService;
import com.fdt.model.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fdt.contant.UserContant.ADMIN_ROLE;
import static com.fdt.contant.UserContant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author fdt
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return long 新用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        //先在userRegisterRequest里拿到数据，判定是否为空，如果有空的，直接返回null
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)){
            return null;
        }
        long result= userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return user
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        //先在userRegisterRequest里拿到数据，判定是否为空，如果有空的，直接返回null
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        User user=userService.userLogin(userAccount, userPassword,request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            return null;
        }
        int result= userService.userLogout(request);
        return ResultUtils.success(result);
    }


    @GetMapping("current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser =(User) userObj;
        if (currentUser == null){
            return null;
        }
        long userId=currentUser.getId();
        // TODO 校验用户是否合法
        User user=userService.getById(userId);
        User safetyUser= userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }


    /**
     * 用户搜索
     * @param username
     * @param request
     * @return 脱敏后的user
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request){
        if (!isAdmin(request)){
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list= userList.stream().map(user ->{
            user.setUserPassword(null);
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    /**
     * 用户删除
     * @param id
     * @param request
     * @return boolen
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        if (!isAdmin(request)){
            return null;
        }
        if (id<=0){
            return null;
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private BaseResponse<Boolean> isAdmin(HttpServletRequest request){
        // 仅管理员可删除
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        boolean result= user != null && user.getUserRole() == ADMIN_ROLE;
        return ResultUtils.success(result);
    }
}
