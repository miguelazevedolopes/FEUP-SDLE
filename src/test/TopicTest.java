import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class TopicTest{
    Topic topic;

    @Before
    public void setUp(){
        topic=new Topic();
    }

    @Test
    public void testPublish(){
        Message dummyMessage = new Message(MessageType.PUT, "TEST_ID","TEST_TOPIC","TEST_CONTENT");

        topic.subscribe("SUBSCRIBER_ID1");

        topic.publish(dummyMessage);

        assertEquals(1, topic.messages.size());
        assertEquals(dummyMessage.getContent(), topic.messages.get(dummyMessage.getID()).getContent());
    }

    @Test
    public void testSubscribeAndUnsubscribe(){
        topic.subscribe("SUBSCRIBER_ID1");
        topic.subscribe("SUBSCRIBER_ID2");

        assertEquals(2, topic.subscribers.size());

        topic.unsubscribe("SUBSCRIBER_ID1");

        assertEquals(1, topic.subscribers.size());

        topic.unsubscribe("SUBSCRIBER_ID2");

        assertEquals(0, topic.subscribers.size());

    }

    @Test
    public void testGetSubscriberMessage(){
        topic.subscribe("SUBSCRIBER_ID1");
        topic.subscribe("SUBSCRIBER_ID2");

        topic.unsubscribe("SUBSCRIBER_ID1");

        Message dummyMessage = new Message(MessageType.PUT, "TEST_ID","TEST_TOPIC","TEST_CONTENT");

        topic.publish(dummyMessage);

        Message responseMessage1=topic.getMessage("SUBSCRIBER_ID2");

        assertEquals( dummyMessage,responseMessage1);

        Message responseMessage2=topic.getMessage("SUBSCRIBER_ID1");
        
        assertEquals(null, responseMessage2);
    }

    @Test
    public void testUpdateSubscriberNextMessage(){
        topic.subscribe("SUBSCRIBER_ID1");

        Message dummyMessage = new Message(MessageType.PUT, "TEST_ID","TEST_TOPIC","TEST_CONTENT");

        topic.publish(dummyMessage);

        Message responseMessage=topic.getMessage("SUBSCRIBER_ID1");

        assertEquals( dummyMessage,responseMessage);

        topic.updateSubscriberNextMessage("SUBSCRIBER_ID1");

        responseMessage=topic.getMessage("SUBSCRIBER_ID1");

        assertEquals(null, topic.getMessage("SUBSCRIBER_ID1"));
    }
    
}
