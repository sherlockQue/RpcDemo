package com.server;

import com.registry.Center;

import com.server.ioc.config.Ioc;
import com.server.ioc.config.annotation.InterService;
import com.server.ioc.config.annotation.Service;
import com.server.ioc.core.BeanContainer;
import common.util.ClassUtil;
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
import java.util.ArrayList;
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


  private String address;

  public NettyServer(String address) {
    this.address = address;

  }

  /**
   * 存放接口名字与服务对象
   */
  private Map<String, Object> handleMap = new HashMap<String, Object>();

  /**
   * 服务启动器
   */
  @Override
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

      String[] addr = address.split(":");

      ChannelFuture channelFuture = b.bind(addr[0], Integer.valueOf(addr[1])).sync();
      if (channelFuture.isSuccess()) {
        System.out.println("server success[" + address + "]");
      }
    } catch (InterruptedException e) {
      System.out.println("启动失败");
    }
  }

  /**
   * 加载bean
   */
//  public void startIoc() {
//    BeanContainer beanContainer = BeanContainer.getInstance();
//    beanContainer.loadBeans();
//    System.out.println("bean加载成功");
//    new Ioc().doIoc();
//
//    Set set = beanContainer.getClassesByAnnotation(Service.class);
//    if (!set.isEmpty()) {
//      Iterator it = set.iterator();
//      while (it.hasNext()) {
//
//        Object o = (((Class<?>) it.next()).getGenericInterfaces())[0];
//        String interfaceName = ((Class) o).getName();
//        Set s = beanContainer.getClassesBySuper((Class) o);
//        Iterator i = s.iterator();
//        while (i.hasNext()) {
//          Object im = i.next();
//          handleMap.put(interfaceName, im);
//
//        }
//      }
//    }
//
//  }

  /**
   * 发现服务
   */
  public List<String> searchService() {

    List<String> interfaceNames = new ArrayList<>();
    Set<Class<?>> classSet = ClassUtil.getPackageClass("com.comon.service");

    Iterator it = classSet.iterator();
    while (it.hasNext()) {
      Class clazz = (Class) it.next();
      if (clazz.isAnnotationPresent(InterService.class)) {
        interfaceNames.add(clazz.getName());
      }
    }
    return interfaceNames;
  }
}
