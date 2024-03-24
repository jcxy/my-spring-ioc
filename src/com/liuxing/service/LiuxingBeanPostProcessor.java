package com.liuxing.service;

import com.liuxing.spring.BeanPostProcessor;
import com.liuxing.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author CVToolMan
 * @create 2024/3/24 13:35
 * 以更加灵活的方式干涉bean的创建过程
 */
@Component
public class LiuxingBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Object bean) {
        if ("userService".equals(beanName)){
            Object proxyInstance = Proxy.newProxyInstance(LiuxingBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("切面逻辑");
                    return method.invoke(bean,args);
                }
            });
            return proxyInstance;
        }
        return bean;
//        if ("userService".equals(beanName)){
//            System.out.println("userService BeanPostProcessor postProcessBeforeInitialization");
//        }
    }

    @Override
    public void postProcessAfterInitialization(String beanName, Object bean) {
        if ("userService".equals(beanName)){
            System.out.println("userService BeanPostProcessor postProcessBeforeInitialization");
        }
    }
}
