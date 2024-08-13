//package com.fdt.mapper;
//
//import com.fdt.model.domain.User;
//import com.fdt.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.StopWatch;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//
//@SpringBootTest
//public class InsertUsers {
////    @Resource
////    private UserMapper userMapper;
//
//    @Resource
//    private UserService userService;
//
//    @Test
//    public void insertUsers() {
//        StopWatch watch = new StopWatch();
//        watch.start();
//        final int INSERT_NUM = 100000;
//        //先将数据放到列表里，然后批量插入
//        List<User> userList = new ArrayList<>();
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("假雨");
//            user.setUserAccount("fakerain");
//            user.setAvatarUrl("https://gw.alipayobjects.com/zos/rmsportal/eeHMaZBwmTvLdIwMfBpg.png");
//            user.setGender(0);
//            user.setUserPassword("12345678");
//            user.setPhone("123");
//            user.setEmail("123@qq.com");
//            user.setTags("[]");
//            user.setProfile("0");
//            user.setUserRole(0);
//            user.setPlanetCode("12345");
//            userList.add(user);
//        }
//        //分批，每次插入一百条
//        userService.saveBatch(userList, 10000);
//        watch.stop();
//        System.out.println(watch.getTotalTimeMillis());
//    }
//
//    /**
//     * 批量插入用户
//     */
//
////  建立自己的线程池
//    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
//    @Test
//    public void doConcurrencyInsertUsers() {
//        StopWatch watch = new StopWatch();
//        watch.start();
//        final int INSERT_NUM = 100000;
//        int batchSize = 5000;
//        int groupSize = INSERT_NUM / batchSize;
//        //分10组，每组1万条
//        int j = 0;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        for (int i = 0; i < groupSize; i++) {
//            List userList = new ArrayList<>();
//            while (true) {
//                j++;
//                User user = new User();
//                user.setUsername("假雨");
//                user.setUserAccount("fakerain");
//                user.setAvatarUrl("https://gw.alipayobjects.com/zos/rmsportal/eeHMaZBwmTvLdIwMfBpg.png");
//                user.setGender(0);
//                user.setUserPassword("12345678");
//                user.setPhone("123");
//                user.setEmail("123@qq.com");
//                user.setTags("[]");
//                user.setProfile("0");
//                user.setUserRole(0);
//                user.setPlanetCode("12345");
//                userList.add(user);
//                if(j%batchSize==0){
//                    break;
//                }
//            }
//            //异步执行数据插入操作，每个任务batchSize条
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                System.out.println("threadName="+Thread.currentThread().getName());
//                userService.saveBatch(userList, batchSize);
//            });
//            futureList.add(future);
//        }
//        //在上面的插入操作结束前阻塞，防止异步直接计时结束，让我们错误预估时间。
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//            watch.stop();
//            System.out.println(watch.getTotalTimeMillis());
//        }
//        //先禁用了yml文件的日志打印
//    }
