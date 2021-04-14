package springApplication.service;

import core.Component;

/**
 * @author: ruifeng.wu
 * @email: RuifengWu93@gmail.com
 * @date: 2021/4/14 22:38
 **/
@Component("beanPostProcessor")
public class BeanPostProcessor implements core.BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("postProcessBeforeInitialization");
        if ("userService".equals(beanName)) {
            ((UserService) bean).setBeanName("hahaha");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("postProcessAfterInitialization");
        return bean;
    }
}
