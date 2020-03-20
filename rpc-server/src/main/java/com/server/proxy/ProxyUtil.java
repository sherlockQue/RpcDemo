package com.server.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author fsq
 * 封装动态代理实现
 */
public class ProxyUtil {

  private ServerProxy serverProxy;

  public ProxyUtil(ServerProxy serverProxy) {
    this.serverProxy = serverProxy;
  }

  /**
   * 通过代理模式实现类
   */
  public Object invoke(Class<?> s, Class<?>[] c) {

    return Proxy.newProxyInstance(s.getClassLoader(), c, serverProxy);
  }

  /**
   * 调用方法,调用由客户端请求的方法，
   * @param methodName  方法名
   * @param parameters  参数列表
   * @param parameterTypes 参数类型
   * @return 返回指定方法的返回值
   */
  public Object invokeMethod( String methodName,Object[] parameters , Class<?>[] parameterTypes) {


    try {
      Object  clazz = serverProxy.getClazz();
      Method method = clazz.getClass().getDeclaredMethod(methodName, parameterTypes);
      Object result = method.invoke(clazz,parameters);

      return result;
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
      return null;
  }
}
