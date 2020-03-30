package com.server;

import com.server.ioc.core.BeanContainer;
import com.server.proxy.ServerProxy;
import com.server.proxy.ProxyUtil;

import common.util.protocol.RpcRequest;
import common.util.protocol.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.Iterator;
import java.util.Set;


/**
 * @author fsq
 */
@Sharable
public class NettyServerHandle extends SimpleChannelInboundHandler<Object> {

  /**
   * 一个列表，保存已连接的客户端
   */
  public static final ChannelGroup GROUP = new DefaultChannelGroup(
      GlobalEventExecutor.INSTANCE);

  /**
   * 通知处理器最后的 channelRead0() 是当前批处理中的最后一条 消息时调用
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    System.out.println("接收结束。");
    super.channelReadComplete(ctx);
    // 4
    ctx.flush();
  }

  /**
   * 异常处理
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    try {
      Channel incoming = ctx.channel();
      System.out.println("用户：" + incoming.remoteAddress() + " 退出");
      if (null != cause) {
        cause.printStackTrace();
      }
      if (null != ctx) {
        ctx.close();
      }
    } catch (Exception e) {

    }
  }


  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object s) {

    try {
      if (s instanceof RpcRequest) {

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(((RpcRequest) s).getRequestId());

        Object result = readHandle((RpcRequest) s);
        rpcResponse.setResult(result);
        channelHandlerContext.channel().writeAndFlush(rpcResponse);
      }

    } catch (Exception e) {
      System.out.println("error2");
    }
  }

  /**
   * 当有客户端连接进来，加到列表
   */
  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    //获取连接的channel
    Channel incomming = ctx.channel();
    System.out.println(incomming.remoteAddress() + "加入");
    //通知所有已经连接到服务器的客户端，有一个新的通道加入
    for (Channel channel : GROUP) {
      channel.writeAndFlush("[SERVER]-" + incomming.remoteAddress() + "加入\n");
    }
    GROUP.add(ctx.channel());
  }


  /**
   * 调用相对应的方法
   * @param rpcRequest 请求信息
   * @return 返回结果
   */
  private Object readHandle(RpcRequest rpcRequest) {



      BeanContainer beanContainer = BeanContainer.getInstance();


      //根据接口名字从bean中拿到具体实例
      Object seInterface = beanContainer.getBeanClass(rpcRequest.getClassName());

        ServerProxy serverProxy = new ServerProxy(seInterface);

        ProxyUtil proxyUtil = new ProxyUtil(serverProxy);
        Object m = proxyUtil
            .invoke(serverProxy.getClazz().getClass(), serverProxy.getClazz().getClass().getInterfaces());

        //执行方法
        Object result = proxyUtil.invokeMethod(rpcRequest.getMethodName(),
            rpcRequest.getParameters(),
            rpcRequest.getParameterTypes());

        return result;


  }

}
