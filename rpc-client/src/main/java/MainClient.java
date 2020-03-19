
import com.client.RpcClientProxy;
import common.ioc.bean.HelloService;

public class MainClient {


  public static void main(String []args){

    RpcClientProxy rpcProxy =new RpcClientProxy("127.0.0.1",8585);
    HelloService helloService =rpcProxy.create(HelloService.class);
    String s =helloService.Hello();
    System.out.println(s);
  }
}
