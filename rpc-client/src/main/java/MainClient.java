
import com.client.RpcClientProxy;
import com.client.centerproxy.CenterClientProxy;
import common.ioc.bean.HelloService;

public class MainClient {


    public static void main(String[] args) {

        MainClient mainClient = new MainClient();
        mainClient.test();
       // mainClient.test2();

    }

    public void test() {

        CenterClientProxy centerClientProxy = new CenterClientProxy("127.0.0.1:2181");
        HelloService helloService = (HelloService) centerClientProxy.invoke("HelloService");
        helloService.Hello();
    }

    public void test2(){
        String addressName = "127.0.0.1:8585";
        RpcClientProxy rpcProxy = new RpcClientProxy(addressName);
        HelloService helloService = rpcProxy.create(HelloService.class);
        String s = helloService.Hello();
        System.out.println(s);

    }
}
