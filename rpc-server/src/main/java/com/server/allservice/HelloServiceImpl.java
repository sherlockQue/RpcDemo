package com.server.allservice;


import com.comon.service.HelloService;
import com.server.ioc.config.annotation.Service;

@Service
public class HelloServiceImpl implements HelloService {


  @Override
  public String Hello() {
    System.out.println("hello world！");
    return "OK,I'm fine";
  }
}
