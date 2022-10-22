import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;

public class ServiceTest{
    Server server;
    Publisher publisher1,publisher2;
    Subscriber subscriber1,subscriber2;
    ZContext zContext;
    
    @Before
    public void setUp() {        
        File stateFile = new File("state");
        if(stateFile.exists()){
            while(!stateFile.delete());
        } 
        server = new Server(new ZContext());
        publisher1 = new Publisher(new ZContext(), "PUBLISHER_ID_1");
        publisher2 = new Publisher(new ZContext(), "PUBLISHER_ID_2");
        subscriber1 = new Subscriber(new ZContext(), "SUBSCRIBER_ID_1");
        subscriber2 = new Subscriber(new ZContext(), "SUBSCRIBER_ID_2");
    }

    @After
    public void cleanUp() throws InterruptedException{
        server.stopServer();
        publisher1.closeSocket();
        subscriber1.closeSocket();
        while(server.isAlive());
        File stateFile = new File("state");
        if(stateFile.exists()){
            while(!stateFile.delete());
        }     
    }

    @Test
    public void testSubscribePutGet() throws InterruptedException{
        server.start();

        subscriber1.subscribe("Music");

        assertEquals(1, server.getTopics().size());

        publisher1.put("Music", "I really love music");

        assertEquals(1, server.getTopics().get("Music").messages.size());

        subscriber1.get("Music");

        assertEquals(0, server.getTopics().get("Music").messages.size());
    }

    @Test
    public void testGetWithoutSubscription(){
        server.start();

        subscriber1.subscribe("Music");

        publisher1.put("Music", "I really love music");
    
        assertEquals(1, server.getTopics().get("Music").messages.size());

        subscriber2.get("Music");

        assertEquals(1, server.getTopics().get("Music").messages.size());

    }

    @Test
    public void testGetWithoutMessages(){
        server.start();

        subscriber1.subscribe("Music");

        assertEquals(1, server.getTopics().size());

        subscriber1.get("Music");

    }

    @Test
    public void testSaveAndRestoreStateToFile(){
        server.start();

        subscriber1.subscribe("Music");

        publisher1.put("Music", "I really love music");

        assertEquals(1, server.getTopics().get("Music").messages.size());

        server.stopServer();

        try {
            server.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        server = new Server(new ZContext());

        server.start();

        assertEquals(1, server.getTopics().get("Music").messages.size());

    }

    @Test
    public void testGetAfterUnsubscribe(){
        server.start();

        subscriber1.subscribe("Music");

        assertEquals(1, server.getTopics().size());

        publisher1.put("Music", "I really love music");

        assertEquals(1, server.getTopics().get("Music").messages.size());

        subscriber1.unsubscribe("Music");

        subscriber1.get("Music");

        assertEquals(0, server.getTopics().get("Music").messages.size());
    }

    @Test
    public void testDoubleSubscribe(){

        server.start();

        subscriber1.subscribe("Music");

        subscriber1.subscribe("Music");

        assertEquals(1, server.getTopics().get("Music").subscribers.size());
    }

    @Test
    public void testUnsubscribe(){

        server.start();

        subscriber1.subscribe("Music");

        assertEquals(1, server.getTopics().get("Music").subscribers.size());


        subscriber1.unsubscribe("Music");

        assertEquals(0, server.getTopics().get("Music").subscribers.size());
    }

    @Test
    public void testDoubleUnsubscribe(){

        server.start();

        subscriber1.subscribe("Music");

        subscriber1.unsubscribe("Music");
        subscriber1.unsubscribe("Music");


        assertEquals(0, server.getTopics().get("Music").subscribers.size());
    }

    @Test
    public void testUnsubscribeNonExistentTopic(){

        server.start();

        subscriber1.unsubscribe("Music");

        assertEquals(0, server.getTopics().size());

    }

    @Test
    public void testSubscribeNonExistentTopic(){

        server.start();

        subscriber1.subscribe("Music");

        assertEquals(1, server.getTopics().size());

    }

    @Test
    public void testRealisticScenario(){
        server.start();

        subscriber1.subscribe("Music");
        subscriber2.subscribe("Music");

        assertEquals(1, server.getTopics().size());

        subscriber1.subscribe("Football");

        assertEquals(2, server.getTopics().size());

        publisher1.put("Music", "I really love music");
        publisher1.put("Music", "I also love the beach");

        subscriber1.get("Music");

        assertEquals(2, server.getTopics().get("Music").messages.size());

        publisher2.put("Music", "I hate music");

        subscriber1.get("Music");
        subscriber1.get("Music");

        assertEquals(3, server.getTopics().get("Music").messages.size());

        subscriber2.get("Music");
        subscriber2.get("Music");
        subscriber2.get("Music");

        assertEquals(0, server.getTopics().get("Music").messages.size());
    }

    @Test
    public void testServerInterruptions(){
        server.start();

        subscriber1.subscribe("Music");
        subscriber2.subscribe("Music");

        assertEquals(1, server.getTopics().size());

        subscriber1.subscribe("Football");

        assertEquals(2, server.getTopics().size());

        publisher1.put("Music", "I really love music");

        server.stopServer();

        publisher1.put("Music", "I also love the beach");

        while(server.isAlive());

        server = new Server(new ZContext());

        server.start();

        assertEquals(2, server.getTopics().get("Music").messages.size());

        subscriber1.get("Music");

        publisher2.put("Music", "I hate music");


        subscriber1.get("Music");
        subscriber1.get("Music");

        assertEquals(3, server.getTopics().get("Music").messages.size());

        subscriber2.get("Music");
        subscriber2.get("Music");
        subscriber2.get("Music");

        assertEquals(0, server.getTopics().get("Music").messages.size());
    }

}
