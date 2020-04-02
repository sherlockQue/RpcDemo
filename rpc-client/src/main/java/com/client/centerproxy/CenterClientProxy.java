package com.client.centerproxy;
import com.client.NettyClient;
import com.client.ioc.config.annotation.Component;
import com.registry.Center;
import common.util.protocol.RpcRequest;
import common.util.sharedata.ShareData;
import java.util.List;
import java.util.Map;

/**
 * @author fsq 通过注册中心查找可提供服务的服务器
 */

@Component
public class CenterClientProxy {

  /**
   * 目前写死，具体到时候可以调整成配置文件
   */
  private String registryAddress = "127.0.0.1:2181";
  private Center center;
  private volatile Map<String, List<String>> nodeMap;

  public CenterClientProxy() {
    center = new Center(registryAddress);
    nodeMap = center.getServer();

    new Thread(() -> {
      while (true) {
        if (center.discover()) {
          System.out.println("节点发生改变，更新服务");
          nodeMap = center.getServer();
        }
      }
    }).start();
  }

  public Object invoke(String serverName,RpcRequest rpcRequest,ShareData shareData) {

    if (nodeMap.containsKey(serverName)) {
      List<String> serverPort = nodeMap.get(serverName);
      int index = center.getRamdonServer(serverPort.size());

      String[] po = serverPort.get(index).split(":");
      System.out.println("提供服务端口: " + po[0] + ":" + po[1]);

      NettyClient client = new NettyClient(serverPort.get(index),shareData);

      return client.send(rpcRequest);
    }
    return null;
  }


}
