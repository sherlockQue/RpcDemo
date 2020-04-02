package com.registry;

import java.util.List;
import java.util.Map;

/**
 * 注册中心接口
 */
public interface RegistryCenter <T>{

  void start();

  void register(List serviceList,String address);

  T getServer();

  Boolean discover();


}
