import org.zeromq.ZContext;
import org.zeromq.ZMsg;

public class Publisher extends Client{
    public static final String SOCKET_ACCESS="localhost:5560";

    public Publisher(ZContext ctx,String id){
        super(ctx,id,SOCKET_ACCESS);
    }
    public void put (String topic, String message){

        Message msg = new Message(MessageType.PUT,this.id,topic,message);
        ZMsg messageString=msg.createMessage();        

        

        ZMsg reply= sendReceive(messageString);
        if(reply==null){
            System.out.println(this.id+": " + "Failed trying to communicate with server. Gave up.");
            return;
        }

        Message reply_msg = new Message(reply);

        System.out.println(this.id+": " + reply_msg.getMessageType().toString());

    }
}