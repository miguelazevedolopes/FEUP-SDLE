import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;

public class ServiceTest{
    Proxy proxy;
    Publisher publisher1,publisher2;
    Subscriber subscriber1,subscriber2;
    ZContext zContext;
    
    @Before
    public void setUp() {        
        File stateFile = new File("state");
        if(stateFile.exists()){
            stateFile.delete();
        } 
        zContext = new ZContext();
        proxy = new Proxy(zContext);
        publisher1 = new Publisher(zContext, "PUBLISHER_ID_1");
        publisher2 = new Publisher(zContext, "PUBLISHER_ID_2");
        subscriber1 = new Subscriber(zContext, "SUBSCRIBER_ID_1");
        subscriber2 = new Subscriber(zContext, "SUBSCRIBER_ID_2");
    }

    @After
    public void cleanUp() throws InterruptedException{
        proxy.stopProxy();
        publisher1.closeSocket();
        subscriber1.closeSocket();
        File stateFile = new File("state");
        if(stateFile.exists()){
            stateFile.delete();
        } 
    }

    @Test
    public void testSubscribePutGet() throws InterruptedException{
        proxy.start();

        subscriber1.subscribe("Music");

        assertEquals(1, proxy.getTopics().size());

        publisher1.put("Music", "I really love music");

        assertEquals(1, proxy.getTopics().get("Music").messages.size());

        subscriber1.get("Music");

        assertEquals(0, proxy.getTopics().get("Music").messages.size());
    }

    @Test
    public void testGetWithoutSubscription(){
        proxy.start();

        subscriber1.subscribe("Music");

        publisher1.put("Music", "I really love music");
    
        // Como não há nenhum subscriber a mensagem nem é guardada, adicionar um subscriber adicional para isto dar certo
        assertEquals(0, proxy.getTopics().get("Music").messages.size());

        subscriber2.get("Music");

        assertEquals(0, proxy.getTopics().get("Music").messages.size());

    }

    @Test
    public void testGetWithoutMessages(){
        proxy.start();

        subscriber1.subscribe("Music");

        assertEquals(1, proxy.getTopics().size());

        subscriber1.get("Music");

    }

    @Test
    public void testSaveAndRestoreStateToFile(){
        proxy.start();

        subscriber1.subscribe("Music");

        publisher1.put("Music", "I really love music");

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

    }

    @Test
    public void testGetAfterUnsubscribe(){
        proxy.start();

        subscriber1.subscribe("Music");

        assertEquals(1, proxy.getTopics().size());

        publisher1.put("Music", "I really love music");

        assertEquals(1, proxy.getTopics().get("Music").messages.size());

        subscriber1.unsubscribe("Music");

        subscriber1.get("Music");

        assertEquals(0, proxy.getTopics().get("Music").messages.size());
    }

    @Test
    public void testDoubleSubscribe(){

        proxy.start();

        subscriber1.subscribe("Music");

        subscriber1.subscribe("Music");

        assertEquals(1, proxy.getTopics().get("Music").subscribers.size());
    }

    @Test
    public void testUnsubscribe(){

        proxy.start();

        subscriber1.subscribe("Music");

        assertEquals(1, proxy.getTopics().get("Music").subscribers.size());


        subscriber1.unsubscribe("Music");

        assertEquals(0, proxy.getTopics().get("Music").subscribers.size());
    }

    @Test
    public void testDoubleUnsubscribe(){

        proxy.start();

        subscriber1.subscribe("Music");

        subscriber1.unsubscribe("Music");
        subscriber1.unsubscribe("Music");


        assertEquals(0, proxy.getTopics().get("Music").subscribers.size());
    }

    @Test
    public void testUnsubscribeNonExistentTopic(){

        proxy.start();

        subscriber1.unsubscribe("Music");

        assertEquals(0, proxy.getTopics().size());

    }

    @Test
    public void testSubscribeNonExistentTopic(){

        proxy.start();

        subscriber1.subscribe("Music");

        assertEquals(1, proxy.getTopics().size());

    }

    @Test
    public void testRealisticScenario(){
        proxy.start();

        subscriber1.subscribe("Music");
        subscriber2.subscribe("Music");

        assertEquals(1, proxy.getTopics().size());

        subscriber1.subscribe("Football");

        assertEquals(2, proxy.getTopics().size());

        publisher1.put("Music", "I really love music");
        publisher1.put("Music", "I also love the beach");

        subscriber1.get("Music");

        assertEquals(2, proxy.getTopics().get("Music").messages.size());

        publisher2.put("Music", "I hate music");

        subscriber1.get("Music");
        subscriber1.get("Music");

        assertEquals(3, proxy.getTopics().get("Music").messages.size());

        subscriber2.get("Music");
        subscriber2.get("Music");
        subscriber2.get("Music");

        assertEquals(0, proxy.getTopics().get("Music").messages.size());
    }

    @Test
    public void testProxyInterruptions(){
        proxy.start();

        subscriber1.subscribe("Music");
        subscriber2.subscribe("Music");

        assertEquals(1, proxy.getTopics().size());

        subscriber1.subscribe("Football");

        assertEquals(2, proxy.getTopics().size());

        publisher1.put("Music", "I really love music");

        proxy.stopProxy();

        publisher1.put("Music", "I also love the beach");

        while(proxy.isAlive());

        proxy = new Proxy(zContext);

        proxy.start();

        subscriber1.get("Music");

        assertEquals(2, proxy.getTopics().get("Music").messages.size());

        publisher2.put("Music", "I hate music");

        subscriber1.get("Music");
        subscriber1.get("Music");

        assertEquals(3, proxy.getTopics().get("Music").messages.size());

        subscriber2.get("Music");
        subscriber2.get("Music");
        subscriber2.get("Music");

        assertEquals(0, proxy.getTopics().get("Music").messages.size());
    }

}
