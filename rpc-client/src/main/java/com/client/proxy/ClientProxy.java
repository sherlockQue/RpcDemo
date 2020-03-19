package com.client.proxy;

import common.util.protocol.RpcRequest;
import common.util.sharedata.ShareData;
import io.netty.channel.Channel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author fsq
 */
public class ClientProxy implements InvocationHandler {

  private Channel channel;

  private ShareData shareData;

  public ClientProxy(Channel channel,ShareData shareData) {
    this.channel = channel;
    this.shareData = shareData;
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    RpcRequest rpcRequest = new RpcRequest();
    rpcRequest.setMethodName(method.getName());
    rpcRequest.setClassName(method.getDeclaringClass().getName());
    rpcRequest.setParameters(args);
    rpcRequest.setParameterTypes(method.getParameterTypes());
    rpcRequest.setRequestId("1000" + new Random().nextInt(1000));

    channel.writeAndFlush(rpcRequest);
    for(;;){
      if(shareData.getRpcResponse(rpcRequest.getRequestId()) != null){
        break;
      }
    }


    Object a =shareData.getRpcResponse(rpcRequest.getRequestId()).getResult();


    return a;
  }
}
