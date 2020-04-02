package com;


import com.controller.ServerController;
import com.server.ioc.config.Ioc;
import com.server.ioc.core.BeanContainer;

/**
 * 服务器启动器
 * @author fsq
 */
public class MainServerApplication {

  public static void main(String []args){

    BeanContainer beanContainer = BeanContainer.getInstance();
    beanContainer.loadBeans();
    new Ioc().doIoc();
    ServerController serverController = (ServerController)beanContainer.getBean(ServerController.class);

    serverController.start("127.0.0.1:8686");
    serverController.start("127.0.0.1:8484");

  }

}
