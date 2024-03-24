package com.liuxing.spring;

import com.liuxing.service.AppConfig;

import java.beans.Introspector;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CVToolMan
 * @create 2024/3/24 9:29
 */
public class LiuxingApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjectMap = new ConcurrentHashMap<>();
    private ArrayList<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public LiuxingApplicationContext(Class configClass) throws Exception {
        this.configClass = configClass;
        //扫描路径
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            //判断类上是否有@ComponentScan注解，并拿到注解
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            //拿到注解的属性
            String path = componentScan.value();//扫描路径
            //转化成包名
            path = path.replace(".", "/");
            //拿扫描路径下的class文件，要拿class文件就要想到类加载器
            ClassLoader classLoader = LiuxingApplicationContext.class.getClassLoader();
            //通过相对路径拿绝对路径
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            System.out.println(file);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    System.out.println(fileName);

                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));//简单写死
                        className = className.replace("\\", ".");
                        System.out.println(className);
                        Class<?> clazz = classLoader.loadClass(className);//需要类的全限定名
                        if (clazz.isAnnotationPresent(Component.class)) {
                            //BeanPostProcessor处理
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                Object instance = clazz.newInstance();
                                beanPostProcessorList.add((BeanPostProcessor) instance);
                            }
                            Component component = clazz.getAnnotation(Component.class);
                            String beanName = component.value();
                            if (beanName == null || "".equals(beanName)) {
                                beanName = Introspector.decapitalize(clazz.getSimpleName());
                            }
                            //生成BeanDefinition
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scope = clazz.getAnnotation(Scope.class);
                                String scopeVal = scope.value();
                                beanDefinition.setScope(scopeVal);
                            } else {
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    }
                }
            }
        }
        Iterator<Map.Entry<String, BeanDefinition>> iterator = beanDefinitionMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BeanDefinition> next = iterator.next();
            BeanDefinition beanDefinition = next.getValue();
            String beanName = next.getKey();
            if ("singleton".equals(beanDefinition.getScope())) {
                Object bean = createBean(beanName, beanDefinition);
                //保存单例信息
                singletonObjectMap.put(beanName, bean);
            } else {

            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) throws Exception {
        Class clazz = beanDefinition.getType();
        //1、构造函数创建对象
        Object instance = clazz.getConstructor().newInstance();

        //2、属性填充（模拟spring的依赖注入）,简单版的byName注入
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Autowired.class)) {
                f.setAccessible(true);
                f.set(instance, getBean(f.getName()));
            }
        }

        //3、Aware 回调 所有aware的回调实现思路都差不多
        if (instance instanceof BeanNameAware) {
            ((BeanNameAware) instance).setBeanName(beanName);
        }

        //4、初始化前BeanPostProcessor postProcessBeforeInitialization
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            beanPostProcessor.postProcessBeforeInitialization(beanName,instance);
        }
        //5、初始化
        if (instance instanceof InitializingBean) {
            ((InitializingBean) instance).afterPropertiesSet();
        }

        //6、初始化后 BeanPostProcessor postProcessAfterInitialization
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            beanPostProcessor.postProcessAfterInitialization(beanName,instance);
        }
        return instance;
    }

    public Object getBean(String beanName) throws Exception {
        //通过beanName如何知道单例还是多例是否懒加载等bean的定义信息？定义scope注解？又要重新去反射解析class类信息
        // 答案是：BeanDeanDefinition
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (null == beanDefinition) {
            throw new Exception("beanDefinition not present");
        } else {
            if ("singleton".equals(beanDefinition.getScope())) {
                Object bean = singletonObjectMap.get(beanName);
                if (bean == null) {
                    bean = createBean(beanName, beanDefinition);
                }
                return bean;
            } else {
                //多例
                return createBean(beanName, beanDefinition);
            }
        }
    }
}
