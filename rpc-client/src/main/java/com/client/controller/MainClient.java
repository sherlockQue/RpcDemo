package com.client.controller;

import com.client.RpcClientProxy;
import com.comon.service.HelloService;
import com.comon.service.SayService;
import com.client.ioc.config.annotation.Autowired;
import com.client.ioc.config.annotation.Controller;

/**
 * @author fsq Client启动测试类
 */
@Controller
public class MainClient {

  @Autowired
  private HelloService helloService;

  @Autowired
  private SayService sayService;


  /**
   * Rpc框架，有注册中心
   */
  public  void test() {

    String s = helloService.Hello();
    System.out.println(s);
  }

  /**
   * Rpc框架，无注册中心
   */
  public void test2() {
    String addressName = "127.0.0.1:8585";
    RpcClientProxy rpcProxy = new RpcClientProxy(addressName);
    HelloService helloService = rpcProxy.create(HelloService.class);
    String s = helloService.Hello();
    System.out.println(s);

  }
}
