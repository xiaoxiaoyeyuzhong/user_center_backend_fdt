package com.fdt.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fdt.model.domain.User;
import com.fdt.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.management.Query;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

//定时任务，缓存推荐用户 todo 缓存重点用户的推荐用户
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    //每天执行一次，预热推荐用户
    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheRecommendUser() {
        log.info("开始预热推荐用户");
        for (Long userId : mainUserList) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
            String redisKey = String.format("yupao:user:recommend:%s", userId);
            userService.setRedisCache(redisKey, userPage, 30L, TimeUnit.MINUTES);
        }
        log.info("推荐用户预热完毕");
    }
}
