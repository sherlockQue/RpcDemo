import com.client.controller.MainClient;
import com.client.ioc.config.Ioc;
import com.client.ioc.core.BeanContainer;

/**
 * client启动器
 * @author fsq
 */
public class MainClientApplication {

  public static void main(String []args){

    BeanContainer beanContainer =BeanContainer.getInstance();
    beanContainer.loadBeans();
    new Ioc().doIoc();
    MainClient mainClient = (MainClient)beanContainer.getBean(MainClient.class);
    mainClient.test();

  }

}
