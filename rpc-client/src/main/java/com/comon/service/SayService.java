package com.comon.service;


import com.client.ioc.config.annotation.InterService;

@InterService
public interface SayService {

  void  rap(String name);
}
