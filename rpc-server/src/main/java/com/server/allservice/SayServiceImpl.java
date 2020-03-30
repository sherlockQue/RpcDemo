package com.server.allservice;

import com.comon.service.SayService;
import com.server.ioc.config.annotation.Service;

@Service
public class SayServiceImpl implements SayService {

  @Override
  public void rap(String name) {

    System.out.println(name+" yamy");
  }
}
