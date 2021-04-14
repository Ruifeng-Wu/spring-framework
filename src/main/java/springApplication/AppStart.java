package springApplication;

import core.ApplicationContext;
import springApplication.service.UserService;

/**
 * @author: ruifeng.wu
 * @email: RuifengWu93@gmail.com
 * @date: 2021/4/13 21:02
 **/
public class AppStart {
    public static void main(String[] args){
        ApplicationContext applicationContext =new ApplicationContext(AppConfig.class);
        UserService userService = (UserService)applicationContext.getBean("userService");
        System.out.println(userService);
        userService.test();
    }
}
