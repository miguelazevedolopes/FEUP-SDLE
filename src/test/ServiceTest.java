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
    public void testSubscribePutGet() throws InterruptedException{
        proxy.start();

        subscriber.subscribe("Music");

        assertEquals(1, proxy.getTopics().size());

        publisher.put("Music", "I really love music");

        assertEquals(1, proxy.getTopics().get("Music").messages.size());

        subscriber.get("Music");
        proxy.stopProxy();
    }




}
