package com.registry;

/**
 * 注册中心接口
 */
public interface RegistryCenter <T>{

  void start();

  void register(String serverName,String address);

  T getServer();


}
