package com.client.centerproxy;

import com.client.Client;
import com.client.NettyClient;
import com.registry.Center;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author fsq
 * 通过注册中心查找可提供服务的服务器
 */
public class CenterClientProxy {


  private String registryAddress;
  private Center center;
  private Map<String, List<String>> nodeMap;

  public CenterClientProxy(String registryAddress) {
    this.registryAddress = registryAddress;
    center = new Center(this.registryAddress);
    nodeMap = center.getServer();

    new Thread(new Runnable() {
      public void run() {
        while (true) {
          if (center.discover()) {
            System.out.println("节点发生改变，更新服务");
            nodeMap = center.getServer();
          }
        }
      }
    }).start();
  }

  public Object invoke(String serverName) {
    if (nodeMap.containsKey(serverName)) {


      List<String> serverPort = nodeMap.get(serverName);
      int index =getRamdonServer(serverPort.size());

      String[] po = serverPort.get(index).split(":");
      System.out.println("提供服务端口: "+po[0]+":"+po[1]);
      NettyClient client = new NettyClient(serverPort.get(index));

      return client.send(serverName);
    }
    return null;
  }

  /**
   * 随机法实现负载均衡
   * @param size
   * @return
   */
  private int getRamdonServer(int size){
    return new java.util.Random().nextInt(size);
  }


}
