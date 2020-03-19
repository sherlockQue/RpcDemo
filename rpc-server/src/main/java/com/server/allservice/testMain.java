package com.server.allservice;

import common.ioc.bean.HelloService;
import common.ioc.core.BeanContainer;

public class testMain {

  public static void main(String[] args){

    BeanContainer beanContainer = BeanContainer.getInstance();
    beanContainer.loadBeans("com.server.allservice");

    HelloService c = (HelloService)beanContainer.getBean(HelloService.class);
    c.Hello();
  }
}
