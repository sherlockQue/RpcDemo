package com.together;


import com.registry.Center;
import com.server.NettyServer;
import com.server.ioc.config.annotation.Component;
import java.util.List;

@Component
public class ServerCenter {

  private Center center;
  private String addressName = "127.0.0.1:2181";

  public ServerCenter(){
    center = new Center(addressName);
  }


  public void startServer(String address){

    NettyServer server =new NettyServer(address);
    server.start();
    List list = server.searchService();
    center.register(list ,address);

  }

}
