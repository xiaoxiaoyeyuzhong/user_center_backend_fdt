package com.fdt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fdt.common.BaseResponse;
import com.fdt.common.ErrorCode;
import com.fdt.common.ResultUtils;
import com.fdt.exception.BusinessException;
import com.fdt.model.domain.request.UserLoginRequest;
import com.fdt.model.domain.request.UserRegisterRequest;
import com.fdt.service.UserService;
import com.fdt.model.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.fdt.contant.UserContant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author fdt
 */

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser =(User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
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
        if (!userService.isAdmin(request)){
           throw new BusinessException(ErrorCode.NO_AUTH,"用户不是管理员");
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
     * 根据标签搜索用户
     * @param tagNameList
     * @return List<User
     */
    @GetMapping("/search/tags")
//  @RequestParam的required默认为true，但是我们要用自己的异常信息，所以设置为false
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
//      先对传入的参数进行一次判空操作
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 分页搜索用户
     * @return List<User>
     */
    @GetMapping("/recommend")
//  @RequestParam的required默认为true，但是我们要用自己的异常信息，所以设置为false
    public BaseResponse<Page<User>> recommendUsers(
            @RequestParam(required = false, defaultValue = "1") long pageNum,
            @RequestParam(required = false, defaultValue = "8") long pageSize,
            HttpServletRequest request){
        User loginUser=userService.getLoginUser(request);
        if (loginUser == null){
            // todo 用户未登录也可以进入首页，看到默认推荐
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        String redisKey=String.format("yupaofdt:user:recommend:%s",loginUser.getId());
        Page<User> userPage= (Page<User>) userService.getRedisCache(redisKey);
        if (userPage != null){
            //如果缓存中存在对应层级和对应id的数据，直接返回
            return ResultUtils.success(userPage);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //分页查询，current指示当前页码，size指示每页显示的记录数
        Page<User> userList = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        List<User> list= userList.getRecords().stream().map(user ->{
            user.setUserPassword(null);
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
        //先用stream流，对list进行遍历，对每个user进行脱敏操作，然后将结果设置回分页结果中
        userList.setRecords(list);
        //缓存时间固定可能导致缓存雪崩 todo 修改缓存过期时间
        userService.setRedisCache(redisKey,userList,1000*60*10, TimeUnit.MILLISECONDS);
        return ResultUtils.success(userList);
    }

    /**
     * 更新用户
     * @param user 用户信息
     * @return Integer
     */
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        //1.校验参数是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.校验用户是否登录
        User loginUser=userService.getLoginUser(request);
        //3.检验权限
        //4.触发更新
        int result= userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 用户删除
     * @param id
     * @param request
     * @return boolen
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"用户不是管理员");
        }
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"要删除的用户不存在");
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }


}
