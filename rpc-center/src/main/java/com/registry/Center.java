package com.registry;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event;


/**
 * @author fsq
 */
public class Center implements RegistryCenter {


  /**
   * 根节点
   */
  public final static String BASE_PATH = "/registry";

  /**
   * 保存服务端register的服务
   */
  private volatile   Map<String, List<String>> nodeMap;


  public ZooKeeper zk = null;
  /**
   * 地址
   */
  private String registryAddress;

  /**
   * 加锁，防止Zookeeper未连接就创建节点而报错
   */
  final CountDownLatch countDownLatch = new CountDownLatch(1);

  public Center(String registryAddress) {
    this.registryAddress = registryAddress;

    try {
      /**
       * 连接Zookeeper
       */
      zk = new ZooKeeper(this.registryAddress, 20000, watchedEvent -> {
        if (Event.KeeperState.SyncConnected.equals(watchedEvent.getState())) {
          //System.out.println("connect success!");
          countDownLatch.countDown();
        }
        try {
          zk.getChildren(BASE_PATH, true);
          nodeMap = null;
          System.out.println("服务节点改变.");
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      });
      //循环监听节点
      zk.getChildren(BASE_PATH, true);

      /**
       * 锁，防止未连接就进行操作
       */
      if (ZooKeeper.States.CONNECTING.equals(zk.getState())) {
        System.out.println("连接中");
        countDownLatch.await();
      }
      System.out.println("connect success！");
    } catch (IOException | InterruptedException | KeeperException e) {
      e.printStackTrace();
    }

    try {
      /**
       * 若根节点不存在，创建根节点
       */
      if (zk != null) {
        if (zk.exists(BASE_PATH, false) == null) {
          zk.create(BASE_PATH,
              "".getBytes(),
              ZooDefs.Ids.OPEN_ACL_UNSAFE,
              CreateMode.PERSISTENT);

        }
      }
    } catch (KeeperException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void start() {

  }

  /**
   * 注册方法
   *
   * @param serverName 服务名字，例如："commom.ioc.bean.HelloService."
   * @param address 服务器地址，例如 "127.0.0.1:8585"
   */
  @Override
  public void register(String serverName, String address) {

    registerHandle(serverName, address);

  }

  /**
   * 注册方法处理器，参数解析参考注册方法
   */
  private void registerHandle(String serverName, String address) {

    String path = "/registry/" + serverName + "/provider/" + address;
    /**
     * 创建节点
     * 最终节点形式如： /registry/common.ioc.bean.HelloService/provider/127.0.0.1:8585
     */
    if (zk != null) {

      try {

        if (zk.exists(BASE_PATH + "/" + serverName, false) == null) {
          zk.create(BASE_PATH + "/" + serverName, "111".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
          System.out.println("create1");
        }

        if (zk.exists(BASE_PATH + "/" + serverName + "/provider", false) == null) {
          zk.create(BASE_PATH + "/" + serverName + "/provider", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
              CreateMode.PERSISTENT);
          System.out.println("create2");
        }
      /**
       * 创建服务节点
       */
        if (zk.exists(path, false) == null) {
          zk.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
              (i, s, o, s1) -> {
                if (i == KeeperException.Code.OK.intValue()) {
                  System.out.println("创建节点成功" + path);
                  try {
                    List<String> node = zk.getChildren(BASE_PATH + "/" + serverName + "/provider", new Watcher() {

                      /**
                       * 实现节点监听
                       * @param watchedEvent
                       */
                      @Override
                      public void process(WatchedEvent watchedEvent) {
                        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                          System.out.println("提供服务端口改变");
                          nodeMap = null;
                        }
                      }
                    });

                      nodeMap = getData();

                  } catch (KeeperException e) {
                    e.printStackTrace();
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }
              }, "callback data");


        }

      } catch (KeeperException e) {
        System.out.println("error: Zookeeper 创建节点失败");
      } catch (InterruptedException e) {
        System.out.println("error: Zookeeper 创建节点失败");
      }


    }

  }

  /**
   * 从Zookeeper获取服务列表
   */
  private Map<String, List<String>> getData() {
    Map<String, List<String>> dataMap = new ConcurrentHashMap<>(16);
    if (zk != null) {
      try {
        List<String> interfaceNames = zk.getChildren(BASE_PATH, false);
        if (interfaceNames != null) {
          for (String interfaceName : interfaceNames) {
            String path = BASE_PATH + "/" + interfaceName + "/provider";
            if (zk.exists(path, false) != null) {
              List<String> addressName = zk.getChildren(path, false);
              dataMap.put(interfaceName, addressName);
            }
          }
          return dataMap;
        }

      } catch (KeeperException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
    return null;

  }

  /**
   * 客户端获取服务列表
   *
   * @return Map<interfaceName       ,       List   <   address>>
   */
  @Override
  public Map<String, List<String>> getServer() {

      nodeMap = getData();

    return nodeMap;
  }

  /**
   * 监听Zookeeper，节点发生改变，则重新获取数据.
   */
  @Override
  public Boolean discover() {


    if (nodeMap == null){
      return true;
    }
      return false;
  }
}
