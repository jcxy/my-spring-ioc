package com.liuxing.spring;

/**
 * @author CVToolMan
 * @create 2024/3/24 13:33
 */
public interface BeanPostProcessor {
    public Object postProcessBeforeInitialization(String beanName,Object bean);
    public void postProcessAfterInitialization(String beanName,Object bean);
}
