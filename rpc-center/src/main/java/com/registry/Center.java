package com.registry;


import static java.lang.Thread.sleep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.*;


/**
 * @author fsq
 */
public class Center implements RegistryCenter {

    private CountDownLatch latch = new CountDownLatch(1);

    public final static String BASE_PATH = "/registry";

    public final static String DATA_PATH = "/registry/";

    public Map<String, List<String>> nodeMap;

    public ZooKeeper zk = null;
    /**
     * 地址
     */
    private String registryAddress;
    final CountDownLatch countDownLatch = new CountDownLatch(1);

    public Center(String registryAddress) {
        this.registryAddress = registryAddress;

        try {
            zk = new ZooKeeper(registryAddress, 20000, new Watcher() {
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

    @Override
    public void register(String serverName, String address) {

        registerHandle(serverName, address);

    }

    private void registerHandle(String serverName, String address) {


        String path = "/registry/" + serverName + "/provider/" + address;
        if (zk != null) {

            try {

                if (zk.exists(BASE_PATH + "/" + serverName, false) == null) {
                    zk.create(BASE_PATH + "/" + serverName, "111".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    System.out.println("create1");
                }

                if (zk.exists(BASE_PATH + "/" + serverName + "/provider", false) == null) {
                    zk.create(BASE_PATH + "/" + serverName + "/provider", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    System.out.println("create2");
                }

                if (zk.exists(path, false) == null) {
                    zk.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
                        @Override
                        public void processResult(int i, String s, Object o, String s1) {
                            if (i == KeeperException.Code.OK.intValue()) {
                                System.out.println("创建节点成功" + path);
                                try {
                                    List<String> node = zk.getChildren(BASE_PATH + "/" + serverName + "/provider", new Watcher() {
                                        @Override
                                        public void process(WatchedEvent watchedEvent) {
                                            if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                                                System.out.println("子节点改变");

                                            }
                                        }
                                    });
                                    if (node != null) {
                                        nodeMap = getData();
                                    }
                                } catch (KeeperException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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

    public Map<String, List<String>> getData() {
        Map<String, List<String>> dataMap = new ConcurrentHashMap<>();
        if (zk != null) {
            try {
                List<String> interfaceNames = zk.getChildren(BASE_PATH, false);
                if (interfaceNames != null) {
                    for (String interfaceName : interfaceNames) {
                        String path = BASE_PATH + "/" + interfaceName + "/provider";
                        List<String> addressName = zk.getChildren(path, false);
                        dataMap.put(interfaceName, addressName);
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
     * 客户端获取服务信息
     *
     * @return Map<interfaceName               ,               List       <       address>>
     */
    @Override
    public Map<String, List<String>> getServer() {

        if (nodeMap == null) {
            nodeMap = getData();
        }
        return nodeMap;
    }
}
