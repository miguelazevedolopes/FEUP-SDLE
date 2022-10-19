import org.zeromq.ZFrame;
import org.zeromq.ZMsg;

import zmq.ZMQ;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private String topic = null;
    private String content = null;


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
        this.topic = Objects.equals(topic, "") ? null : topic;;
    }

    /**
     * Message constructor with no need of argument
     */
    public Message(MessageType messageType, String clientID, String topic, String content ) {
        this.messageType = messageType.name();
        this.clientID = clientID;
        this.topic = Objects.equals(topic, "") ? null : topic;
        this.content = Objects.equals(content, "") ? null : content;
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

        String id = this.id == null ? "null" : this.id;

        String header = this.messageType +
                " " +
                id;

        msg.addString(header);
        msg.addString(this.topic == null ? "null" : this.topic);
        msg.addString(this.content == null ? "null" : this.content);
        return msg;
        
    }

    public ZMsg createIdentifiedMessage() {
        //Creates a string with like "messageType id clientID "
        ZMsg msg = createMessage();
        msg.wrap(new ZFrame(clientID));
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
        

        this.id = generateHash(sb.toString());
        
    }

    public static String generateHash(String valToHash) {
        MessageDigest md=null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] hash = md.digest(valToHash.getBytes(ZMQ.CHARSET));
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
 
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
 
        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }
 
        return hexString.toString();
    }

    /**
     * Decomposes the message received in the form of ZMsg into the Message class attributes
     * @param msg Message in form of ZMsg
     */
    private void decomposeMessage(ZMsg msg) {
        String firstFrame = msg.popString();
        String secondFrame = msg.popString();

        String topic;
        if(secondFrame.equals("")){
            this.clientID=firstFrame;
            String header = msg.popString();

            List<String> elements = Arrays.asList(header.split(" "));
            this.messageType = elements.get(0);
            this.id = elements.get(1).equals("null") ? null : elements.get(1);
            topic = msg.popString();
        }
        else{
            String header = firstFrame;

            List<String> elements = Arrays.asList(header.split(" "));
            this.messageType = elements.get(0);
            this.id = elements.get(1).equals("null") ? null : elements.get(1);
            topic = secondFrame;
        }
      
         
        this.topic = topic.equals("null") ? null : topic;
        String content = msg.popString();
        this.content = content.equals("null") ? null : content;
    }

    // Getters
    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public MessageType getMessageType() {
        return MessageType.valueOf(this.messageType);
    }

    public String getClientID() {
        return clientID;
    }

    public String getID() {
        return id;
    }
}
