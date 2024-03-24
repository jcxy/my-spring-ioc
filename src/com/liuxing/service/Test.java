package com.liuxing.service;

import com.liuxing.spring.LiuxingApplicationContext;

import java.lang.reflect.InvocationTargetException;

/**
 * @author CVToolMan
 * @create 2024/3/24 9:27
 */
public class Test {
    public static void main(String[] args) throws Exception {
        //创建spring容器
        LiuxingApplicationContext context = new LiuxingApplicationContext(AppConfig.class);
//        System.out.println(context.getBean("userService"));
//        System.out.println(context.getBean("userService"));
//        System.out.println(context.getBean("userService"));
//        System.out.println(context.getBean("orderService"));
        UserInterface userService = (UserService)context.getBean("userService");
        userService.test();
    }
}
