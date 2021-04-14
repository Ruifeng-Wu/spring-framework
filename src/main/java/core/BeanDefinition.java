package core;

/**
 * @author: ruifeng.wu
 * @email: RuifengWu93@gmail.com
 * @date: 2021/4/13 21:55
 **/
public class BeanDefinition {

    private Class clazz;
    private ScopeEnum scope;

    public BeanDefinition(Class clazz) {
        this.clazz = clazz;
    }

    public BeanDefinition(Class clazz, ScopeEnum scope) {
        this.clazz = clazz;
        this.scope = scope;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public ScopeEnum getScope() {
        return scope;
    }

    public void setScope(ScopeEnum scope) {
        this.scope = scope;
    }
}
