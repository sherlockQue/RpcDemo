package com.server.allservice;

import common.ioc.bean.SayService;
import common.ioc.config.annotation.Service;

@Service
public class SayServiceImpl implements SayService {

  public void rap() {
    System.out.println("yamy");
  }
}
