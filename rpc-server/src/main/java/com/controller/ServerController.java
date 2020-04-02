package com.controller;

import com.server.ioc.config.annotation.Autowired;
import com.server.ioc.config.annotation.Controller;
import com.together.ServerCenter;

@Controller
public class ServerController {

  @Autowired
  private ServerCenter serverCenter;

  public void start(String adderss){

    serverCenter.startServer(adderss);
  }

}
