package core;

import core.annotation.Autowired;
import core.annotation.Component;
import core.annotation.ComponentScan;
import core.annotation.Scope;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
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
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;
        //解析配置类
        scanBeans(configClass);
        Set<Map.Entry<String, BeanDefinition>> entries = beanDefinitionMap.entrySet();

        entries.stream().forEach(beanDefinitionEntry -> {
            if ("singleton".equals(beanDefinitionEntry.getValue().getScope())) {
                Object bean = createBean(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
                singletonObjects.put(beanDefinitionEntry.getKey(), bean);
            }
        });
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            //依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(field.getName());
                    field.setAccessible(true);
                    try {
                        field.set(instance, bean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            //aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }
            for (BeanPostProcessor beanPostProcessor :
                    beanPostProcessors) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }
            //初始化
            if (instance instanceof BeanNameAware) {
                ((InitializingBean) instance).afterPropertiesSet();
            }
            //BeanPostProcessor
            for (BeanPostProcessor beanPostProcessor :
                    beanPostProcessors) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
                                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                    BeanPostProcessor instance = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                    beanPostProcessors.add(instance);
                                }
                                Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                String beanName = componentAnnotation.value();
                                BeanDefinition beanDefinition = new BeanDefinition(clazz);
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                } else {
                                    beanDefinition.setScope(ScopeEnum.SINGLETON);
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);

                            }

                        } catch (ClassNotFoundException | NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
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
        return createBean(beanName, beanDefinition);
    }
}
