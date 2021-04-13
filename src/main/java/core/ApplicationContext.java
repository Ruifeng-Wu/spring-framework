package core;

import javafx.beans.binding.ObjectExpression;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ruifeng.wu
 * @email: RuifengWu93@gmail.com
 * @date: 2021/4/13 20:58
 **/
public class ApplicationContext {

    private final Class configClass;
    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;
        //解析配置类
        scanBeans(configClass);
        Set<Map.Entry<String, BeanDefinition>> entries = beanDefinitionMap.entrySet();

        entries.stream().forEach(beanDefinitionEntry -> {
            if ("singleton".equals(beanDefinitionEntry.getValue().getScope())) {
                Object bean = createBean(beanDefinitionEntry.getValue());
                singletonObjects.put(beanDefinitionEntry.getKey(), bean);
            }
        });
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scanBeans(Class configClass) {
        ComponentScan componentScan = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScan.value();

        //扫描bootstrap--jre/lib ext--jre/ext/lib app--classpath
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path.replace(".", "/"));
        File fileDirectory = new File(resource.getFile());
        if (fileDirectory.isDirectory()) {
            File[] files = fileDirectory.listFiles();
            Arrays.stream(files)
                    .forEach(file ->
                    {
                        String fileName = file.getAbsolutePath();
                        try {
                            Class<?> clazz = classLoader.loadClass(fileName.substring(
                                    fileName.indexOf("springApplication"),
                                    fileName.indexOf(".class")
                            ).replace("/", "."));
                            if (clazz.isAnnotationPresent(Component.class)) {
                                //解析类prototype singleton

                                Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                String beanName = componentAnnotation.value();
                                BeanDefinition beanDefinition = new BeanDefinition(clazz);
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);

                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    });

        }
    }

    public Object getBean(String beanName) {

        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new NullPointerException();
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if ("singleton".equals(beanDefinition.getScope())) {
            Object bean = singletonObjects.get(beanName);
            return bean;
        }
        return createBean(beanDefinition);
    }
}
