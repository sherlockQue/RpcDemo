package com.comon.service;


import com.server.ioc.config.annotation.InterService;

@InterService
public interface SayService {

  void  rap(String name);
}
