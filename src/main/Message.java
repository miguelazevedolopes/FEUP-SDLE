import org.zeromq.ZMsg;


import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a message.
 * The message protocol must have two essential fields: type, messageID ,senderID
 * If the message has no ID, the ID field will be sent as "null"
 * <p>
 * Types of message (type - description - fields)
 * SUB - subscribe - cmd, messageID ,senderID, topic
 * UNSUB - unsubscribe - cmd, messageID ,sender_ID, topic
 * PUT - publish - cmd, messageID ,senderID, topic, content
 * GET - get - cmd, messageID ,senderID, topic
 * GET_RESP - get response - cmd, messageID, senderID, topic, content
 * ACK - acknowledge that put was successful - cmd, messageID, senderID
 *
 */

public class Message {
    private String id = null;
    private String cmd;
    private String senderID;
    private String topic = "";
    private String content = "";


    public Message(MessageType cmd, String senderID) {
        this.cmd = cmd.name();
        this.senderID = senderID;
    }

    /**
     * Constructor
     *
     * @param cmd      type of Message
     * @param senderID sender ID
     * @param topic    topic of the message
     */
    public Message( MessageType cmd,String senderID, String topic ) {
        this.cmd = cmd.name();
        this.senderID = senderID;
        this.topic = topic;
    }

    /**
     * Message constructor with no need of argument
     */
    public Message(MessageType cmd, String senderID, String topic, String content ) {
        this.cmd = cmd.name();
        this.senderID = senderID;
        this.topic = topic;
        this.content = content;

        this.createID();

    }


    /**
     * Constructor
     * @param message Receives a message in the form of a ZMsg
     * 
     */
    public Message(ZMsg message){
        decomposeMessage(message);
    }

    /**
     * Creates a ZMsg from the Message class attributes
     * @return A ZMsg instance with the Message class attributes
     */
    public ZMsg createMessage() {

        //Creates a string with like "cmd id senderID args"
        StringBuilder sb = new StringBuilder();
        sb.append(this.cmd);
        sb.append(" ");
        sb.append(this.id);
        sb.append(" ");
        sb.append(this.senderID);

        String header = sb.toString();

        ZMsg msg = new ZMsg();
        msg.addString(header);
        if(!this.topic.equals("")) msg.addString(this.topic);
        if(!this.content.equals("")) msg.addString(this.content);

        return msg;
        
    }

    /**
     * Creates an ID with the date and the senderID
     */
    private void createID(){
        StringBuilder sb = new StringBuilder();
        Timestamp date = new Timestamp(System.currentTimeMillis());
        sb.append(date.getTime());
        sb.append(this.senderID);
        
        this.id = Integer.toString(sb.toString().hashCode());

    }

    /**
     * Decomposes the message received in the form of ZMsg into the Message class attributes
     * @param msg Message in form of ZMsg
     */
    private void decomposeMessage(ZMsg msg) {
       String header = msg.popString();
       List<String> elements = Arrays.asList(header.split(" "));
       
       this.cmd = elements.get(0);
       this.id = elements.get(1).equals("null")
        ? null
        : elements.get(1);

       this.senderID = elements.get(2);

       String topic = msg.popString();
       if(topic == null){
           this.topic = "";
           this.content = "";
           return;
       }else{
           this.topic = topic;
       }

        String content = msg.popString();
        if(content == null){
            this.content = "";
        }else{
            this.content = topic;
        }

    }

    // Getters

    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public MessageType getCmd() {
        return MessageType.valueOf(cmd);
    }

    public String getSenderID() {
        return senderID;
    }

    public String getID() {
        // criei esta funçao só para nao me dar error warnings
        return this.id;
    }







}
