package com.liuxing.service;

import com.liuxing.spring.*;

/**
 * @author CVToolMan
 * @create 2024/3/24 9:28
 */
@Component("userService")
@Scope("prototype")
public class UserService implements UserInterface,BeanNameAware, InitializingBean {
    @Autowired
    private OrderService orderService;

    private String beanName;

    private String xxx;

    public void xxxx(){

    }

    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {

    }
}
