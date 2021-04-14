package springApplication.service;

import core.*;
import core.annotation.Autowired;
import core.annotation.Component;
import core.annotation.Scope;

/**
 * @author: ruifeng.wu
 * @email: RuifengWu93@gmail.com
 * @date: 2021/4/13 21:00
 **/
@Component("userService")
@Scope(ScopeEnum.PROTOTYPE)
public class UserServiceImpl implements InitializingBean, BeanNameAware, UserService {
    @Autowired
    private OrderService orderService;

    private String beanName;

    private String name;

    public void test() {
        System.out.println(orderService);
        System.out.println(beanName);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet");
    }
}
