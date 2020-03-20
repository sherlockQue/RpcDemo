package com.client;

public class RpcClientProxy {

  private String addressName;

  public RpcClientProxy(String addressName) {
    this.addressName = addressName;
  }

  public <T> T create(Class<T> interfaceclass) {

    Client client =new NettyClient(addressName,interfaceclass);
    return (T) client.send();
  }



}
