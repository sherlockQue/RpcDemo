package com.registry;


import static java.lang.Thread.sleep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;


/**
 * @author fsq
 */
public class Center implements RegistryCenter {

  private CountDownLatch latch = new CountDownLatch(1);

  public final static String BASE_PATH = "/registry";

  public final static String DATA_PATH = "/registry/";

  public ZooKeeper zk = null;
  /**
   * 地址
   */
  private String registryAddress;
  final CountDownLatch countDownLatch = new CountDownLatch(1);

  public Center(String registryAddress) {
    this.registryAddress = registryAddress;

    try {
      zk = new ZooKeeper(registryAddress, 60000, new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
          if (Watcher.Event.KeeperState.SyncConnected.equals(watchedEvent.getState())) {
            System.out.println("connect success!");
            countDownLatch.countDown();
          }
        }
      });
      if (ZooKeeper.States.CONNECTING.equals(zk.getState())) {
        System.out.println("连接中");
        countDownLatch.await();
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    try {
      if (zk != null) {
        if (zk.exists(BASE_PATH, false) == null) {
          zk.create(BASE_PATH,
              "123".getBytes(),
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

  @Override
  public void register(String serverName, String address) {

    registerHandle(serverName, address);

  }

  private void registerHandle(String serverName, String address) {


    String path = "/registry/" + serverName + "/provider/" + address;
    if (zk != null) {

      try {

        if (zk.exists(BASE_PATH+"/"+serverName, false) == null) {
          zk.create(BASE_PATH+"/"+serverName, "111".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
          System.out.println("create1");
        }

        if (zk.exists(BASE_PATH+"/"+serverName+"/provider", false) == null) {
          zk.create(BASE_PATH+"/"+serverName+"/provider", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
          System.out.println("create2");
        }

        if (zk.exists(path, false) == null) {
          zk.create(path, "112".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
          System.out.println("create3");
        }
        System.out.println("创建节点" + path);
      } catch (KeeperException e) {
        System.out.println("error: Zookeeper 创建节点失败");
      } catch (InterruptedException e) {
        System.out.println("error: Zookeeper 创建节点失败");
      }


    }

  }

  public void getData() {

    if (zk != null) {
      try {
        List<String> list = zk.getChildren(BASE_PATH, false);
        if (list != null) {
          for (String s : list) {
            System.out.println("节点：" + s);
            byte[] by = zk.getData(BASE_PATH + "/" + s, false, null);
            System.out.print("     当前节点数据：" + by);
          }
        }
      } catch (KeeperException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public Map<String, List<String>> getData2() {

    Map<String, List<String>> dataMap = new HashMap<>();
    if (zk != null) {
      try {
        List<String> interfaceNames = zk.getChildren(BASE_PATH, false);
        if (interfaceNames != null) {
          for (String interfaceName : interfaceNames) {
            String path = BASE_PATH + "/" + interfaceName + "/provider";
            List<String> addressName = zk.getChildren(path, false);
            dataMap.put(interfaceName,addressName);

//            for (String name : addressName) {
//              String adderss = path+"/"+name;
//                byte[] bytes = zk.getData(adderss,false,null);
//            }
          }
          return  dataMap;
        }

      } catch (KeeperException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
    return null;
  }

  private void createNode(ZooKeeper zk, String data) {

    try {
      byte[] bytes = data.getBytes();

      String path = zk.create(DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

    } catch (KeeperException | InterruptedException e) {
      System.out.println("error: Zookeeper 创建节点失败");
    }

  }

  @Override
  public Object getServer() {

    return null;
  }
}
