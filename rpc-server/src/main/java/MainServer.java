import com.registry.Center;
import com.server.NettyServer;
import com.server.Server;

public class MainServer {

  public static void main(String []args){


    Server server =new NettyServer(8585,"127.0.0.1",new Center("127.0.0.1:2181"));
    try {
      server.startIoc();
      server.start();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
