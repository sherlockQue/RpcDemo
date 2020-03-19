package com.server.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author fsq
 * @param <T>
 */
public class ServerProxy<T> implements InvocationHandler {

  private T clazz;

  public ServerProxy(T clazz){
    this.clazz =clazz;
  }

  public T  getClazz(){
    return clazz;
  }


  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

   Object i = method.invoke(clazz,args);

    return i;
  }
}
