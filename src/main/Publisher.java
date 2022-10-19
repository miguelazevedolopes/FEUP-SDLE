import org.zeromq.ZContext;
import org.zeromq.ZMsg;

public class Publisher extends SocketOwner{
    public static final String SOCKET_ACCESS="localhost:5560";

    public Publisher(ZContext ctx,String id){
        super(ctx,id,SOCKET_ACCESS);
    }
    public void put (String topic, String message){
        setup();
        connect();

        Message msg = new Message(MessageType.PUT,this.id,topic,message);
        ZMsg messageString=msg.createMessage();
        messageString.send(socketZMQ);
        

        ZMsg reply = ZMsg.recvMsg(socketZMQ);

        Message reply_msg = new Message(reply);

        System.out.println(reply_msg.getMessageType().toString());


    }


}