import org.zeromq.ZMsg;


import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//TODO Question if the prefer to delimiter message per Frames or spaces

/**
 * This class represents a message.
 * The message protocol must have two essential fields: type, messageID ,senderID
 * and the remaining fields or depend on the type of message and will be added to args
 * If the message has no ID, the ID field will be sent as "null"
 * <p>
 * Types of message (type - description - fields)
 * SUB - subscribe - cmd, messageID ,sender_ID, topic
 * UNSUB - unsubscribe - cmd, messageID ,sender_ID, topic
 * PUT - publish - cmd, messageID ,sender_ID, topic, content
 * GET - get - cmd, messageID ,sender_ID, topic
 * GET_RESP - get response - cmd, messageID, sender_ID, topic, content
 * PUT_ACK - acknowledge that put was successful - cmd, messageID, sender_ID
 * GET_ACK - acknowledge that get was successful received by Subscriber - cmd, , messageID, sender_ID, topic
 */
public class Message {
    private String id = null;
    private String cmd;
    private String senderID;
    private List<String> args;
    private String message = "";

   
    /**
     * Constructor
     *
     * @param cmd      type of Message
     * @param senderID sender ID
     * @param args     arguments that depend on type of message
     */
    public Message( String cmd,String senderID, List<String> args) {
        this.cmd = cmd;
        this.senderID = senderID;
        this.args = args;
    }

    /**
     * Message constructor with no need of argument
     */
    public Message(String cmd, String senderID) {
        this.cmd = cmd;
        this.senderID = senderID;
        this.args = new ArrayList<>();
    }

    public Message(String cmd, String senderID, String arg) {
        this.cmd = cmd;
        this.senderID = senderID;
        this.args = new ArrayList<>();
        this.args.add(arg);
    }

    /**
     * If we add a date argument, it will calculate its id and be an identified message
     */
    public Message(String cmd, String senderID, List<String> args, Timestamp date){
        this.cmd = cmd;
        this.senderID = senderID;
        this.args = args;

        this.createID(date);
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
        sb.append(" ");
        for (String arg : args) {
            sb.append(args);
            sb.append(" ");
        }

        this.message = sb.toString();
        ZMsg msg = new ZMsg();
        msg.add(message.trim());

        return msg;
        
    }

    /**
     * Creates an ID with the date and the 
     * @param date
     */
    private void createID(Timestamp date){
        StringBuilder sb = new StringBuilder();
        sb.append(date.getTime());
        sb.append(this.senderID);
        try {
            this.id = EncoderSHA.encode(sb.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Decomposes the message received in the form of ZMsg into the Message class attributes
     * @param msg Message in form of ZMsg
     */
    private void decomposeMessage(ZMsg msg) {
       this.message = msg.popString();
       List<String> elements = Arrays.asList(this.message.split(" "));
       
       this.cmd = elements.get(0);
       this.id = elements.get(1).equals("null")
        ? null
        : elements.get(1);

       this.senderID = elements.get(2);

       this.args = new ArrayList<>();

       //If there are no arguments, no need to continue
       if(elements.size() ==3 ) return;

       //There are arguments so let's pick em up
       this.args = elements.subList(3, elements.size());     

    }

    // Getters
    public List<String> getArgs() {
        return args;
    }

    public String getCmd() {
        return cmd;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getID() {
        // criei esta funçao só para nao me dar error warnings
        return this.id;
    }







}
