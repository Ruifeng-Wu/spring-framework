package springApplication.service;

import core.Autowired;
import core.Component;
import core.Scope;
import core.ScopeEnum;

/**
 * @author: ruifeng.wu
 * @email: RuifengWu93@gmail.com
 * @date: 2021/4/13 21:00
 **/
@Component("userService")
@Scope(ScopeEnum.PROTOTYPE)
public class UserService {
    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);
    }
}
