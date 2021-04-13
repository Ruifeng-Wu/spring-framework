package springApplication;

import core.ApplicationContext;

/**
 * @author: ruifeng.wu
 * @email: RuifengWu93@gmail.com
 * @date: 2021/4/13 21:02
 **/
public class AppStart {
    public static void main(String[] args){
        ApplicationContext applicationContext =new ApplicationContext(AppConfig.class);
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
    }
}
