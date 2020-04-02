package com.client;


import com.client.proxy.ClientProxy;
import com.registry.Center;
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
import java.util.List;
import java.util.Map;


/**
 * @author fsq
 */
public class NettyClient implements Client {

  private String addressName;
  private NettyClientHandle nettyClientHandle;
  private ShareData shareData;
  private Channel channel;

  /**
   * 有注册中心
   * @param addressName 要连接的服务端地址
   * @param shareData 数据共享
   */
  public NettyClient(String addressName, ShareData shareData) {
    this.addressName = addressName;
    this.shareData = shareData;
    try {
      connect();
    } catch (InterruptedException e) {
      System.out.println("连接服务端失败，addr："+addressName);
    }

  }

  @Override
  public void connect() throws InterruptedException {

    if (shareData == null) {
      shareData = new ShareData();
    }
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

    String[] po = addressName.split(":");
    channel = bootstrap.connect(po[0], Integer.valueOf(po[1])).sync().channel();

  }
  /**
   * 发送信息
   */
  public Object send(RpcRequest rpcRequest) {

    channel.writeAndFlush(rpcRequest);
    // 因异步调用，阻塞，直到拿到值
    for (; ; ) {
      if (shareData.getRpcResponse(rpcRequest.getRequestId()) != null) {
        break;
      }
    }
    return shareData.getRpcResponse(rpcRequest.getRequestId()).getResult();
  }



}
