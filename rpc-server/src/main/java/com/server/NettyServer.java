package com.server;

import com.registry.Center;
import com.server.allservice.HelloServiceImpl;
import common.ioc.bean.HelloService;
import common.ioc.config.Ioc;
import common.ioc.config.annotation.Service;
import common.ioc.core.BeanContainer;
import common.util.Serializer.JsonSerializer;
import common.util.codec.RpcDecoder;
import common.util.codec.RpcEncoder;
import common.util.protocol.RpcRequest;
import common.util.protocol.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fsq
 */
public class NettyServer implements Server {

  private int port;
  private String host;
  private Center center;


  public NettyServer(int port, String host, Center center) {
    this.port = port;
    this.host = host;
    this.center = center;
  }

  /**
   * 存放接口名字与服务对象
   */
  private Map<String, Object> handleMap = new HashMap<String, Object>();

  /**
   * 服务启动器
   */
  public void start() {

    NioEventLoopGroup boos = new NioEventLoopGroup();
    NioEventLoopGroup workGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(boos, workGroup)
          .channel(NioServerSocketChannel.class)
          // 配置入站、出站事件handler
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
              ch.pipeline()
                  //.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                  .addLast(new RpcEncoder(RpcResponse.class, new JsonSerializer()))
                  .addLast(new RpcDecoder(RpcRequest.class, new JsonSerializer()))
                  .addLast(new NettyServerHandle());
            }

          })
          // determining the number of connections queued
          .option(ChannelOption.SO_BACKLOG, 128)
          //开启心跳包活机制，就是客户端、服务端建立连接处于ESTABLISHED状态，超过2小时没有交流，机制会被启动
          .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

      ChannelFuture channelFuture = b.bind(host, port).sync();
      //注册地址

      if (center != null) {
        for (String interfaceName : handleMap.keySet()) {

          center.register(interfaceName, host+":"+port);

        }

      }

      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
    }
  }

  /**
   * 加载bean
   */
  public void startIoc() {
    BeanContainer beanContainer = BeanContainer.getInstance();
    beanContainer.loadBeans("com.server.allservice");
    new Ioc().doIoc();

    Set set = beanContainer.getClassesByAnnotation(Service.class);
    if (!set.isEmpty()) {
      Iterator it = set.iterator();
      while (it.hasNext()) {

        Object o = (((Class<?>) it.next()).getGenericInterfaces())[0];
        String interfaceName = ((Class) o).getName();
        Set s = beanContainer.getClassesBySuper((Class) o);
        Iterator i = s.iterator();
        while (i.hasNext()) {
          Object im = i.next();
          handleMap.put(interfaceName, im);

        }
      }
    }

  }
}
