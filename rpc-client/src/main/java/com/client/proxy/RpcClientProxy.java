package com.client.proxy;

import com.client.ioc.config.annotation.Component;
import java.lang.reflect.Proxy;

/**
 * @author fsq
 */
@Component
public class RpcClientProxy {

  public RpcClientProxy() {}

  /**
   * 在启动时，通过bean注入这个代理方法
   * @param interfaceclass
   * @param <T>
   * @return
   */
  public <T> T create(Class<T> interfaceclass) {

    return (T) Proxy.newProxyInstance(interfaceclass.getClassLoader(), new Class[]{interfaceclass}, new ClientProxy());
  }



}
