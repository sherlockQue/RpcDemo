package com.client.centerproxy;

import com.client.Client;
import com.client.NettyClient;
import com.registry.Center;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CenterClientProxy {


    private String registryAddress;
    private Center center;
    private Map<String, List<String>> nodeMap;

    public CenterClientProxy(String registryAddress) {
        this.registryAddress = registryAddress;
        center = new Center("127.0.0.1:2181");
        nodeMap = center.getServer();

    }

    public Object invoke(String serverName) {
        if (nodeMap.containsKey(serverName)) {

            List<String> serverPort = nodeMap.get(serverName);
            String[] po = serverPort.get(0).split(":");
            Client client = new NettyClient(serverPort.get(0));
            return ((NettyClient) client).send(serverName);
        }
        return null;
    }
}
