package com.client;


import com.client.proxy.ClientProxy;
import common.ioc.bean.HelloService;
import common.util.Serializer.JsonSerializer;
import common.util.codec.RpcDecoder;
import common.util.codec.RpcEncoder;
import common.util.protocol.RpcRequest;
import common.util.protocol.RpcResponse;
import common.util.sharedata.ShareData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.lang.reflect.Proxy;


/**
 * @author fsq
 */
public class NettyClient implements Client {

  private String host;
  private int port;
  private NettyClientHandle nettyClientHandle;
  private ShareData shareData;
  private Class<?> interfaceclass;
  private Channel channel;


  public NettyClient(String host,int port,Class<?> interfaceclass) {
    this.port = port;
    this.host =host;
    this.interfaceclass = interfaceclass;
    try {
      connect();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void connect() throws InterruptedException {

    shareData = new ShareData();
    nettyClientHandle = new NettyClientHandle(shareData);
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(eventLoopGroup)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<Channel>() {

          @Override
          protected void initChannel(Channel arg0) throws Exception {
            ChannelPipeline pipeline = arg0.pipeline();
            pipeline.addLast(new RpcEncoder(RpcRequest.class, new JsonSerializer()));
            pipeline.addLast(new RpcDecoder(RpcResponse.class, new JsonSerializer()));
            pipeline.addLast(nettyClientHandle);
          }
        });
    channel = bootstrap.connect(host, port).sync().channel();


  }

  public Object send() {

//    HelloService helloService = (HelloService) Proxy
//        .newProxyInstance(HelloService.class.getClassLoader(), new Class[]{HelloService.class},
//            new ClientProxy(channel,shareData));
    System.out.println(interfaceclass);

    return  Proxy.newProxyInstance(interfaceclass.getClassLoader(), new Class[]{interfaceclass}, new ClientProxy(channel,shareData));

  }



}
