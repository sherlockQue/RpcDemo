
import com.client.RpcClientProxy;
import com.client.centerproxy.CenterClientProxy;
import common.ioc.bean.HelloService;
import common.ioc.bean.SayService;

/**
 * @author fsq
 *  Client启动测试类
 */
public class MainClient {


    public static void main(String[] args) {

        MainClient mainClient = new MainClient();
        mainClient.test();
       // mainClient.test2();

    }

    /**
     * Rpc框架，有注册中心
     */
    public void test() {

        CenterClientProxy centerClientProxy = new CenterClientProxy("127.0.0.1:2181");
        HelloService helloService = (HelloService) centerClientProxy.invoke("common.ioc.bean.HelloService");
        String s = helloService.Hello();
        System.out.println(s);

        SayService sayService = (SayService) centerClientProxy.invoke("common.ioc.bean.SayService");
        sayService.rap("hhh");
    }

    /**
     * Rpc框架，无注册中心
     */
    public void test2(){
        String addressName = "127.0.0.1:8585";
        RpcClientProxy rpcProxy = new RpcClientProxy(addressName);
        HelloService helloService = rpcProxy.create(HelloService.class);
        String s = helloService.Hello();
        System.out.println(s);

    }
}
