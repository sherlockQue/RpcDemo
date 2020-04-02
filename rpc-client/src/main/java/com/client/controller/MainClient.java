package com.client.controller;


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

    System.out.println(helloService.Hello());
    System.out.println(helloService.Hello());
    System.out.println(helloService.Hello());

  }


}
