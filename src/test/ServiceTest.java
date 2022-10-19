import org.junit.Test;
import org.zeromq.ZContext;

import junit.framework.TestCase;

public class ServiceTest extends TestCase{
    Proxy proxy;
    Publisher publisher;
    Subscriber subscriber;
    
    @Override
    public void setUp(){
        
        ZContext zContext = new ZContext();
        proxy = new Proxy(zContext);
        publisher = new Publisher(zContext, "PUBLISHER_ID");
        subscriber = new Subscriber(zContext, "SUBSCRIBER_ID");
    }

    @Test
    public void testSubscribe() throws InterruptedException{
        System.out.println("Start");
        proxy.start();
        System.out.println("After 1 sec sleep");

        subscriber.subscribe("Music");
        publisher.put("Music", "I really love music");
        subscriber.get("Music");
        proxy.stopProxy();
    }
}
