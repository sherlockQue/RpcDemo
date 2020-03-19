package com.server.allservice;


import common.ioc.bean.HelloService;
import common.ioc.config.annotation.Service;



@Service
public class HelloServiceImpl implements HelloService {


  public String Hello() {
    System.out.println("hello world！");
    return "OK,I'm fine";
  }
}
