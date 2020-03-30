package com.server.ioc.core;


import com.server.ioc.ClassUtil;
import com.server.ioc.config.annotation.Component;
import com.server.ioc.config.annotation.Controller;
import com.server.ioc.config.annotation.Service;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author fsq
 */
public class BeanContainer {

  private BeanContainer(){}

  private static BeanContainer beanContainer = new BeanContainer();

  public static BeanContainer getInstance() {

    return beanContainer;
  }

  /**
   * 存放所有Bean的Map
   */
  private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

  /**
   * 获取Bean实例
   */
  public Object getBean(Class<?> clz) {
    if (null == clz) {
      return null;
    }
    return beanMap.get(clz);
  }

  /**
   * 获取所有Bean集合
   */
  public Set<Object> getBeans() {
    return new HashSet<>(beanMap.values());
  }

  /**
   * 添加一个Bean实例
   */
  public Object addBean(Class<?> clz, Object bean) {
    return beanMap.put(clz, bean);
  }

  /**
   * 移除一个Bean实例
   */
  public void removeBean(Class<?> clz) {
    beanMap.remove(clz);
  }

  /**
   * Bean实例数量
   */
  public int size() {
    return beanMap.size();
  }

  /**
   * 所有Bean的Class集合
   */
  public Set<Class<?>> getClasses() {
    return beanMap.keySet();
  }

  /**
   * 通过注解获取Bean的Class集合
   */
  public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
    return beanMap.keySet()
        .stream()
        .filter(clz -> clz.isAnnotationPresent(annotation))
        .collect(Collectors.toSet());
  }

  /**
   * 通过实现类或者父类获取Bean的Class集合
   */
  public Set<Class<?>> getClassesBySuper(Class<?> superClass) {
    return beanMap.keySet()
        .stream()
        .filter(superClass::isAssignableFrom)
        .filter(clz -> !clz.equals(superClass))
        .collect(Collectors.toSet());
  }

  /**
   * 是否加载Bean
   */
  private boolean isLoadBean = false;

  /**
   * 加载bean的注解列表,并实例化放到beanMap里面
   */
  private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
      = Arrays.asList(Component.class, Service.class, Controller.class);

  public void loadBeans(String basePackage) {
//    if (isLoadBean()) {
//      System.out.println("bean已经加载");
//      return;
//    }
    Set<Class<?>> classSet = ClassUtil.getPackageClass(basePackage);
    classSet.stream()
        .filter(clz -> {
          for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
            if (clz.isAnnotationPresent(annotation)) {
              return true;
            }
          }
          return false;
        })
        .forEach(clz -> beanMap.put(clz, ClassUtil.newInstance(clz)));
    //isLoadBean = true;
  }

  /**
   * 是否加载Bean
   */
  public boolean isLoadBean() {
    return isLoadBean;
  }

  /**
   * 获得bean实例
   * @param interfaceName 接口
   * @return
   */
  public Object getBeanClass(String interfaceName) {

      Set<Object> classSet = getBeans();
      Iterator it = classSet.iterator();
      while (it.hasNext()) {
        Object cass = it.next();
        Class<?> o =  cass.getClass();
        for(Class<?> cl : o.getInterfaces()){
          if(cl.getName().equals(interfaceName)){
            return cass;
          }
        }
      }
      return null;
  }

}