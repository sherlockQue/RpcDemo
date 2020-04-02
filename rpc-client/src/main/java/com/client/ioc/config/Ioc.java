package com.client.ioc.config;


import com.client.proxy.RpcClientProxy;
import common.util.ClassUtil;
import com.client.ioc.config.annotation.Autowired;
import com.client.ioc.config.annotation.InterService;
import com.client.ioc.core.BeanContainer;
import java.lang.reflect.Field;
import java.util.Optional;

public class Ioc {

  private BeanContainer beanContainer;

  public Ioc(){
    beanContainer = BeanContainer.getInstance();
  }
  /**
   * 执行Ioc
   */
  public void doIoc() {
    //遍历Bean容器中所有的Bean

    for (Class<?> clz : beanContainer.getClasses()) {
      final Object targetBean = beanContainer.getBean(clz);
      Field[] fields = clz.getDeclaredFields();
      //遍历Bean中的所有属性
      for (Field field : fields) {
        // 如果该属性被Autowired注解，则对其注入
        if (field.isAnnotationPresent(Autowired.class)) {
          final Class<?> fieldClass = field.getType();
          Object fieldValue;
          if(fieldClass.isAnnotationPresent(InterService.class)){
             fieldValue = initInterService(fieldClass);
            ClassUtil.setField(field, targetBean, fieldValue);
          }else {
            fieldValue = getClassInstance(fieldClass);
            if (null != fieldValue) {
              ClassUtil.setField(field, targetBean, fieldValue);
            } else {
              throw new RuntimeException("无法注入对应的类，目标类型:" + fieldClass.getName());
            }
          }
        }
      }
    }
  }

  /**
   * 根据InterService实例服务。
   */
  private Object initInterService(Class<?> fieldClass ){

    return ((RpcClientProxy ) beanContainer.getBean(RpcClientProxy.class)).create(fieldClass);
  }

  /**
   * 根据Class获取其实例或者实现类
   */
  private Object getClassInstance(final Class<?> clz) {
    return Optional
        .ofNullable(beanContainer.getBean(clz))
        .orElseGet(() -> {
          Class<?> implementClass = getImplementClass(clz);
          if (null != implementClass) {
            return beanContainer.getBean(implementClass);
          }
          return null;
        });
  }
  /**
   * 获取接口的实现类
   */
  private Class<?> getImplementClass(final Class<?> interfaceClass) {
    return beanContainer.getClassesBySuper(interfaceClass)
        .stream()
        .findFirst()
        .orElse(null);
  }
}
