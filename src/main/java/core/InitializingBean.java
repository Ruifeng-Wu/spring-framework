package core;

/**
 * @author: ruifeng.wu
 * @email: RuifengWu93@gmail.com
 * @date: 2021/4/14 22:28
 **/
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
