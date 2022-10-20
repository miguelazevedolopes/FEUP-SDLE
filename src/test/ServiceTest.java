import org.junit.Test;
import org.zeromq.ZContext;

import junit.framework.TestCase;

public class ServiceTest extends TestCase{
    Proxy proxy;
    Publisher publisher;
    Subscriber subscriber;
    ZContext zContext;
    
    @Override
    public void setUp(){
        zContext = new ZContext();
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

        assertEquals(0, proxy.getTopics().get("Music").messages.size());

        proxy.stopProxy();
    }

    @Test
    public void testUnsubscribeGet(){
        proxy.start();

        publisher.put("Music", "I really love music");

        assertEquals(1, proxy.getTopics().get("Music").messages.size());

        subscriber.get("Music");

        assertEquals(1, proxy.getTopics().get("Music").messages.size());

        proxy.stopProxy();
    }

    @Test
    public void testSaveAndRestoreStateToFile(){
        proxy.start();

        publisher.put("Music", "I really love music");

        assertEquals(1, proxy.getTopics().get("Music").messages.size());

        proxy.stopProxy();

        try {
            proxy.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        proxy = new Proxy(zContext);

        proxy.start();

        assertEquals(1, proxy.getTopics().get("Music").messages.size());

        proxy.stopProxy();

    }

}
