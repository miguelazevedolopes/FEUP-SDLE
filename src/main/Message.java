import org.zeromq.ZMsg;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a message.
 * The message protocol must have two essential fields: type, messageID ,clientID
 * If the message has no ID, the ID field will be sent as "null"
 * <p>
 * Types of message (type - description - fields)
 * SUB - subscribe - messageType, messageID ,clientID, topic
 * UNSUB - unsubscribe - messageType, messageID ,clientID, topic
 * PUT - publish - messageType, messageID ,clientID, topic, content
 * GET - get - messageType, messageID ,clientID, topic
 * GET_RESP - get response - messageType, messageID, clientID, topic, content
 * ACK - acknowledge that put was successful - messageType, messageID, clientID
 * ERROR - error - messageType, messageID, clientID, content
 */

public class Message implements Serializable{
    private String id = null;
    private String messageType;
    private String clientID;
    private String topic = "";
    private String content = "";


    public Message(MessageType messageType, String clientID) {
        this.messageType = messageType.name();
        this.clientID = clientID;
    }

    /**
     * Constructor
     *
     * @param messageType      type of Message
     * @param clientID client ID
     * @param topic    topic of the message
     */
    public Message( MessageType messageType,String clientID, String topic ) {
        this.messageType = messageType.name();
        this.clientID = clientID;
        this.topic = topic;
    }

    /**
     * Message constructor with no need of argument
     */
    public Message(MessageType messageType, String clientID, String topic, String content ) {
        this.messageType = messageType.name();
        this.clientID = clientID;
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

        //Creates a string with like "messageType id clientID "

        ZMsg msg = new ZMsg();
        msg.addString(this.clientID);

        String header = this.messageType +
                " " +
                this.id;

        msg.addString(header);
        msg.addString(this.topic);
        msg.addString(this.content);

        return msg;
        
    }

    /**
     * Creates an ID with the date and the clientID
     */
    private void createID(){
        StringBuilder sb = new StringBuilder();
        Timestamp date = new Timestamp(System.currentTimeMillis());
        sb.append(date.getTime());
        sb.append(this.clientID);
        
        this.id = Integer.toString(sb.toString().hashCode());

    }

    /**
     * Decomposes the message received in the form of ZMsg into the Message class attributes
     * @param msg Message in form of ZMsg
     */
    private void decomposeMessage(ZMsg msg) {
        this.clientID = msg.popString();

       String header = msg.popString();
       List<String> elements = Arrays.asList(header.split(" "));
       
       this.messageType = elements.get(0);
       this.id = elements.get(1).equals("null")
        ? null
        : elements.get(1);

       String topic = msg.popString();
       if(topic == null){
           this.topic = "";
           this.content = "";
           return;
       }else{
           this.topic = topic;

       }

        this.content = msg.popString();


    }

    // Getters
    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public MessageType getMessageType() {
        return MessageType.valueOf(messageType);
    }

    public String getClientID() {
        return clientID;
    }

    public String getID() {
        // criei esta funçao só para nao me dar error warnings
        return this.id;
    }


}
