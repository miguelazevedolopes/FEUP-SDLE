import junit.framework.TestCase;
import org.junit.Test;
import org.zeromq.ZMsg;

public class MessageTest extends TestCase {

    public void testCreateAndDecomposeMessage() {
        //Test 1 - Create a message with a topic and read it to ensure it is correct
        Message message = new Message(MessageType.PUT,"id","topic","content");
        ZMsg zmsg = message.createMessage();
        System.out.println(zmsg.toString());
        Message message2 = new Message(zmsg);
        assertEquals(message.getCmd(),message2.getCmd());
        assertEquals(message.getID(),message2.getID());
        assertEquals(message.getTopic(),message2.getTopic());
        assertEquals(message.getContent(),message2.getContent());

        //Test 2 - Create a message without a topic and read it to ensure it is correct
        Message message3 = new Message(MessageType.PUT,"id","","content");
        ZMsg zmsg2 = message3.createMessage();
        System.out.println(zmsg2.toString());
        Message message4 = new Message(zmsg2);
        assertEquals(message3.getCmd(),message4.getCmd());
        assertEquals(message3.getID(),message4.getID());
        assertEquals(message3.getTopic(),message4.getTopic());
        assertEquals(message3.getContent(),message4.getContent());

        //Test 3 - Create a message without a content and read it to ensure it is correct
        Message message5 = new Message(MessageType.PUT,"id","topic","");
        ZMsg zmsg3 = message5.createMessage();
        System.out.println(zmsg3.toString());
        Message message6 = new Message(zmsg3);
        assertEquals(message5.getCmd(),message6.getCmd());
        assertEquals(message5.getID(),message6.getID());
        assertEquals(message5.getTopic(),message6.getTopic());
        assertEquals(message5.getContent(),message6.getContent());

        //Test 4 - Create a message without a topic or content and read it to ensure it is correct
        Message message7 = new Message(MessageType.PUT,"id","","");
        ZMsg zmsg4 = message7.createMessage();
        System.out.println(zmsg4.toString());
        Message message8 = new Message(zmsg4);
        assertEquals(message7.getCmd(),message8.getCmd());
        assertEquals(message7.getID(),message8.getID());
        assertEquals(message7.getTopic(),message8.getTopic());
        assertEquals(message7.getContent(),message8.getContent());

        //Test 5 - Create a message with a topic and read it to ensure it is correct
        Message message9 = new Message(MessageType.GET,"id","topic");
        ZMsg zmsg5 = message9.createMessage();
        System.out.println(zmsg5.toString());
        Message message10 = new Message(zmsg5);
        assertEquals(message9.getCmd(),message10.getCmd());
        assertEquals(message9.getID(),message10.getID());
        assertEquals(message9.getTopic(),message10.getTopic());
        assertEquals(message9.getContent(),message10.getContent());

        //Test 6 - Create a message without a topic and read it to ensure it is correct
        Message message11 = new Message(MessageType.GET,"id","");
        ZMsg zmsg6 = message11.createMessage();
        System.out.println(zmsg6.toString());
        Message message12 = new Message(zmsg6);
        assertEquals(message11.getCmd(),message12.getCmd());
        assertEquals(message11.getID(),message12.getID());
        assertEquals(message11.getTopic(),message12.getTopic());
        assertEquals(message11.getContent(),message12.getContent());

        //Test 7 - Create a message with a topic and read it to ensure it is correct
        Message message13 = new Message(MessageType.SUB,"id");
        ZMsg zmsg7 = message13.createMessage();
        System.out.println(zmsg7.toString());
        Message message14 = new Message(zmsg7);
        assertEquals(message13.getCmd(),message14.getCmd());
        assertEquals(message13.getID(),message14.getID());
        assertEquals(message13.getTopic(),message14.getTopic());
        assertEquals(message13.getContent(),message14.getContent());


        Message message30 = new Message(MessageType.SUB,"id", "", "");
        ZMsg zmsg30 = message30.createMessage();

        System.out.println(zmsg30.toString());


    }

}