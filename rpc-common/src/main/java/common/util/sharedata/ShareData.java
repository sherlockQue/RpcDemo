package common.util.sharedata;

import common.util.protocol.RpcResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于记录客户端与服务端消息是否一致
 *
 * @author fsq
 */
public class ShareData {


  private volatile Map<String,RpcResponse> stringRpcResponseMap = new ConcurrentHashMap<>();

  public void addRpcResponse(String key,RpcResponse rpcResponse){
    this.stringRpcResponseMap.put(key,rpcResponse);
  }

  public RpcResponse getRpcResponse(String key){
    return this.stringRpcResponseMap.get(key);
  }




}
