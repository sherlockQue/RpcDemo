package com.client;

public class RpcClientProxy {

  private String host;
  private int port;

  public RpcClientProxy(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public <T> T create(Class<T> interfaceclass) {

    Client client =new NettyClient(host,port,interfaceclass);
    return (T) client.send();
  }

}
