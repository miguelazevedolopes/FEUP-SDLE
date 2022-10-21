import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;

public class ServiceTest{
    Proxy proxy;
    Publisher publisher;
    Subscriber subscriber1,subscriber2;
    ZContext zContext;
    
    @Before
    public void setUp() {        
        zContext = new ZContext();
        proxy = new Proxy(zContext);
        publisher = new Publisher(zContext, "PUBLISHER_ID");
        subscriber1 = new Subscriber(zContext, "SUBSCRIBER_ID_1");
        subscriber2 = new Subscriber(zContext, "SUBSCRIBER_ID_2");
    }

    @After
    public void cleanUp() throws InterruptedException{
        proxy.stopProxy();
        publisher.closeSocket();
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

        publisher.put("Music", "I really love music");

        assertEquals(1, proxy.getTopics().get("Music").messages.size());

        subscriber1.get("Music");

        assertEquals(0, proxy.getTopics().get("Music").messages.size());
    }

    @Test
    public void testGetWithoutSubscription(){
        proxy.start();

        subscriber1.subscribe("Music");

        publisher.put("Music", "I really love music");
    
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


    }

}
