package com.client.proxy;
import com.client.centerproxy.CenterClientProxy;
import com.client.ioc.config.annotation.Autowired;
import com.client.ioc.core.BeanContainer;
import common.util.protocol.RpcRequest;
import common.util.sharedata.ShareData;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Random;

/**
 *
 * @author fsq
 */

public class ClientProxy implements InvocationHandler {


  /**
   * 记录服务器返回的信息，达到信息同步
   */
  private ShareData shareData;

  public ClientProxy( ShareData shareData) {

    this.shareData = shareData;
  }

  public ClientProxy(){
    shareData = new ShareData();
  }
  /**
   * ,获取调用方法的形参，参数类型，方法名
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args)   {

    //rpcRequest 赋值
    RpcRequest rpcRequest = new RpcRequest();
    rpcRequest.setMethodName(method.getName());
    rpcRequest.setClassName(method.getDeclaringClass().getName());
    rpcRequest.setParameters(args);
    rpcRequest.setParameterTypes(method.getParameterTypes());
    rpcRequest.setRequestId("1000" + new Random().nextInt(1000));

    CenterClientProxy centerClientProxy = (CenterClientProxy)BeanContainer.getInstance().getBean(CenterClientProxy.class);

    return centerClientProxy.invoke(rpcRequest.getClassName(),rpcRequest,shareData);
  }
}
